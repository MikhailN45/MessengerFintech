package com.study.messengerfintech.data.repository

import android.util.Log
import com.study.messengerfintech.data.model.MessageResponse
import com.study.messengerfintech.data.model.ReactionResponse
import com.study.messengerfintech.data.model.StreamResponse
import com.study.messengerfintech.data.model.TopicResponse
import com.study.messengerfintech.data.model.UserResponse
import com.study.messengerfintech.data.network.NarrowInt
import com.study.messengerfintech.data.network.NarrowStr
import com.study.messengerfintech.data.network.ZulipRetrofitApi
import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.Reaction
import com.study.messengerfintech.domain.model.Stream
import com.study.messengerfintech.domain.model.Topic
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.model.UserStatus
import com.study.messengerfintech.domain.repository.Repository
import com.study.messengerfintech.utils.SendType
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.encodeToJsonElement
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(private val service: ZulipRetrofitApi) : Repository {

    override fun loadStreams(): Single<List<Stream>> =
        service.getStreams()
            .subscribeOn(Schedulers.io())
            .flatMap { streamResponses ->
                val streams = streamResponses.streams
                    .map { streamResponse ->
                        loadTopics(streamResponse.id)
                            .map { topics ->
                                streamResponse.toStream(topics)
                            }
                    }
                Single.zip(streams) { it.toList() as List<Stream> }
                    .doOnError {
                        Log.e("loadStreams", it.message.toString())
                    }
            }

    override fun loadSubscribedStreams(): Single<List<Stream>> =
        service.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .flatMap { streamResponses ->
                val streams = streamResponses.subscriptions
                    .map { streamResponse ->
                        loadTopics(streamResponse.id)
                            .map { topics ->
                                streamResponse.toStream(topics)
                            }
                    }
                Single.zip(streams) { it.toList() as List<Stream> }
                    .doOnError {
                        Log.e("loadSubscribedStreams", it.message.toString())
                    }
            }

    override fun getMessageCountForTopic(stream: Int, topic: String): Single<Int> =
        loadTopicMessages(stream, topic).map { it.size }
            .doOnError { Log.e("getMessagesCountForTopic", it.message.toString()) }
            .onErrorResumeNext { Single.just(0) }

    override fun loadTopics(streamId: Int): Single<List<Topic>> =
        service.getTopicsInStream(streamId)
            .subscribeOn(Schedulers.io())
            .map { it.topics.toTopics() }
            .doOnError {
                Log.e("loadTopics", it.message.toString())
            }


    override fun loadUsers(): Single<List<User>> =
        service.getUsers()
            .subscribeOn(Schedulers.io())
            .map {
                it.members.map { userResponse -> userResponse.toUser() }
            }.flatMap { usersStatusPreload(it) }
            .doOnError {
                Log.e("loadUsers", it.message.toString())
            }

    private fun usersStatusPreload(users: List<User>): Single<List<User>> {
        val statusLoaders = users.map { user ->
            loadStatus(user)
                .doOnError {
                    Log.e("usersStatusPreload", it.message.toString())
                }
        }
        return Single.concatEager(statusLoaders).toList()
    }

    override fun loadStatus(user: User): Single<User> =
        service.getPresence(user.id)
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.presence["aggregated"]?.status?.let { status ->
                    user.status = UserStatus.stringToStatus(status)
                    user
                }
            }.onErrorReturn { user }
            .doOnError {
                Log.e("loadStatus", it.message.toString())
            }

    override fun loadOwnUser(): Single<User> =
        service.getOwnUser()
            .subscribeOn(Schedulers.io())
            .map { it.toUser() }
            .doOnError {
                Log.e("loadOwnUser", it.message.toString())
            }

    override fun loadTopicMessages(stream: Int, topic: String): Single<List<Message>> {
        val narrow = listOf(
            NarrowInt("stream", stream),
            NarrowStr("topic", topic)
        ).map {
            Json.encodeToJsonElement(it)
        }.let {
            JsonArray(it).toString()
        }

        return service.getMessages(narrow = narrow)
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.messages.map { messageResponse ->
                    messageResponse.toMessage(
                        reactions = messageResponse.reactions.map { it.toReaction() }
                    )
                }
            }.map { it.reversed() }
            .doOnError {
                Log.e("loadTopicMessages", it.message.toString())
            }

    }

    override fun loadPrivateMessages(userEmail: String): Single<List<Message>> {
        val narrow = listOf(
            NarrowStr("pm-with", userEmail)
        ).map {
            Json.encodeToJsonElement(it)
        }.let {
            JsonArray(it).toString()
        }

        return service.getMessages(narrow = narrow)
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.messages.map { messageResponse ->
                    messageResponse.toMessage()
                }
            }.map { it.reversed() }
            .doOnError {
                Log.e("loadPrivateMessages", it.message.toString())
            }
    }

    override fun sendMessage(
        type: SendType,
        to: String,
        content: String,
        topic: String
    ): Single<Int> =
        service.sendMessage(type.type, to, content, topic)
            .map { it.id }
            .doOnError {
                Log.e("sendMessage", it.message.toString())
            }


    override fun addEmoji(messageId: Int, emojiName: String): Completable =
        service.addEmojiReaction(messageId, name = emojiName).ignoreElement()
            .subscribeOn(Schedulers.io())
            .doOnError {
                Log.e("addEmoji", it.message.toString())
            }


    override fun deleteEmoji(messageId: Int, emojiName: String): Completable =
        service.deleteEmojiReaction(messageId, name = emojiName).ignoreElement()
            .subscribeOn(Schedulers.io())
            .doOnError {
                Log.e("deleteEmoji", it.message.toString())
            }

    private fun MessageResponse.toMessage(reactions: List<Reaction> = emptyList()): Message =
        Message(
            id = id,
            content = content,
            userId = userId,
            isMine = isMine,
            senderName = senderName,
            timestamp = timestamp,
            avatarUrl = avatarUrl,
            reactions = reactions
        )

    private fun ReactionResponse.toReaction(): Reaction = Reaction(
        userId = userId,
        code = code,
        name = name
    )

    private fun StreamResponse.toStream(topics: List<Topic>): Stream = Stream(
        id = id,
        title = title,
        topics = topics
    )

    private fun TopicResponse.toTopic(): Topic = Topic(
        title = title,
        messageCount = messageCount
    )

    private fun List<TopicResponse>.toTopics(): List<Topic> = map { it.toTopic() }


    private fun UserResponse.toUser(status: UserStatus = UserStatus.Offline): User = User(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        status = status
    )
}