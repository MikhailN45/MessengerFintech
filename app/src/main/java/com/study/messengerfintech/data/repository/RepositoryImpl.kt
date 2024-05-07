package com.study.messengerfintech.data.repository

import android.util.Log
import com.study.messengerfintech.data.database.AppDatabase
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
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.encodeToJsonElement
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    private val service: ZulipRetrofitApi,
    private val database: AppDatabase
) : Repository {

    override fun requestAllStreams(): Completable {
        return service.getStreams()
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { streamResponses ->
                val streams = streamResponses.streams
                    .map { streamResponse ->
                        loadTopics(streamResponse.id)
                            .map { topics ->
                                streamResponse.toStream(topics, false)
                            }
                    }
                Single.zip(streams) { it.toList() as List<Stream> }
                    .flatMapCompletable { streamList ->
                        database.streamDao().insert(streamList)
                    }
                    .doOnError { error: Throwable ->
                        Log.e("requestAllStreams", "${error.message}")
                    }
            }
            .onErrorComplete()
    }

    override fun getAllStreams(): Observable<List<Stream>> {
        return database.streamDao().getAll()
            .toObservable()
            .observeOn(Schedulers.io())
            .flatMap {
                getTopicsFromStreams(it).toObservable()
            }
            .doOnEach {
                Log.d("getAllStreams", "$it")
            }
    }

    override fun requestSubscribedStreams(): Completable {
        return service.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { streamResponses ->
                val streams = streamResponses.subscriptions
                    .map { streamResponse ->
                        loadTopics(streamResponse.id)
                            .map { topics ->
                                streamResponse.toStream(topics, true)
                            }
                    }
                Single.zip(streams) { it.toList() as List<Stream> }
                    .flatMapCompletable { streamList ->
                        database.streamDao().insert(streamList)
                    }
                    .doOnError { error: Throwable ->
                        Log.e("requestSubscribedStreams", "${error.message}")
                    }
            }
            .onErrorComplete()
    }

    override fun getSubscribedStreams(): Observable<List<Stream>> {
        return database.streamDao().getSubscribed()
            .toObservable()
            .observeOn(Schedulers.io())
            .flatMap {
                getTopicsFromStreams(it).toObservable()
            }
            .doOnEach {
                Log.d("getSubscribedStreams", "$it")
            }
    }


    private fun getTopicsFromStreams(streams: List<Stream>): Single<List<Stream>> {
        val streamsWithTopics = streams.map { stream ->
            database.topicDao().getTopicsInStream(stream.id)
                .doOnSuccess { Log.d("getTopicsFromStreams", "${stream.id} $it") }
                .subscribeOn(Schedulers.io())
                .map { topicList -> stream.toStream(topicList) }
        }

        return Single.concatEager(streamsWithTopics).toList()
    }

    override fun getMessageCountForTopic(stream: Int, topic: String): Single<Int> =
        loadTopicMessages(stream, topic, "newest").map { it.size }
            .doOnError { Log.e("getMessagesCountForTopic", it.message.toString()) }
            .onErrorResumeNext { Single.just(0) }

    override fun loadTopics(streamId: Int): Single<List<Topic>> {
        val localAnswer = database.topicDao().getTopicsInStream(streamId)
            .doOnSuccess { Log.d("getTopicsInStream", "$streamId $it") }

        val remoteAnswer = service.getTopicsInStream(streamId)
            .subscribeOn(Schedulers.io())
            .map { it.topics.toTopics(streamId = streamId) }
            .flatMap { topics -> database.topicDao().insert(topics).toSingleDefault(topics) }
            .onErrorResumeNext { localAnswer }

        return remoteAnswer.subscribeOn(Schedulers.io())
    }


    override fun loadUsers(): Observable<List<User>> {
        val localAnswer = database.userDao().getAll()
            .subscribeOn(Schedulers.io())

        val remoteAnswer = service.getUsers()
            .subscribeOn(Schedulers.io())
            .map {
                it.members.map { userResponse -> userResponse.toUser() }
            }.flatMap { usersStatusPreload(it) }
            .doOnSuccess { database.userDao().insert(it) }
            .onErrorResumeNext { error ->
                Log.e("loadUsersRetrofit", "${error.message}")
                localAnswer
            }

        return Single.concat(localAnswer, remoteAnswer).toObservable()
    }


    private fun usersStatusPreload(users: List<User>): Single<List<User>> {
        val usersWithStatus = users.map { user ->
            loadStatus(user)
                .doOnError { error ->
                    Log.e("usersStatusPreload", "${error.message}")
                }
        }

        return Single.concatEager(usersWithStatus).toList()
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
            .doOnError { error ->
                Log.e("getPresence", "${error.message}")
            }

    override fun loadOwnUser(): Single<User> = service.getOwnUser()
        .subscribeOn(Schedulers.io())
        .map { it.toUser() }
        .flatMap { database.userDao().insert(it).toSingleDefault(it) }
        .onErrorResumeNext { database.userDao().getOwnUser() }


    private fun clearMessages(messages: List<Message>): List<Message> {
        if (messages.size <= 50) return messages
        messages.subList(0, messages.size - 50).onEach { message ->
            database.messageDao().delete(message)
        }
        return messages.subList(messages.size - 50, messages.size)
    }

    override fun loadTopicMessages(
        stream: Int,
        topic: String,
        anchor: String
    ): Single<List<Message>> {
        val narrow = listOf(
            NarrowInt("stream", stream),
            NarrowStr("topic", topic)
        ).map {
            Json.encodeToJsonElement(it)
        }.let {
            JsonArray(it).toString()
        }

        val localAnswer =
            database.messageDao().getPublicMessages(stream, topic)
                .map { clearMessages(it) }
                .map { it.reversed() }

        return service.getMessages(narrow = narrow, anchor = anchor)
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.messages.map { messageResponse ->
                    messageResponse.toMessage(
                        reactions = messageResponse.reactions.map { it.toReaction() },
                        streamId = stream,
                        topicTitle = topic
                    )
                }
            }
            .map {
                if (anchor != "newest") it.subList(0, it.size - 1)
                else it
            }
            .map { it.reversed() }
            .flatMap { messages ->
                database.messageDao().insert(messages).toSingleDefault(messages)
            }
            .onErrorResumeNext { localAnswer }
    }

    override fun loadPrivateMessages(userEmail: String, anchor: String): Single<List<Message>> {
        val narrow = listOf(
            NarrowStr("pm-with", userEmail)
        ).map {
            Json.encodeToJsonElement(it)
        }.let {
            JsonArray(it).toString()
        }

        val localAnswer =
            database.messageDao().getPrivateMessages(userEmail)
                .map { clearMessages(it) }
                .map { it.reversed() }

        return service.getMessages(narrow = narrow, anchor = anchor)
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.messages.map { messageResponse ->
                    messageResponse.toMessage(userEmail = userEmail)
                }
            }
            .map {
                if (anchor != "newest") it.subList(0, it.size - 1)
                else it
            }
            .map { it.reversed() }
            .flatMap { messages ->
                database.messageDao().insert(messages).toSingleDefault(messages)
            }
            .onErrorResumeNext { localAnswer }
    }

    override fun sendMessage(
        type: SendType,
        to: String,
        content: String,
        topic: String
    ): Single<Int> =
        service.sendMessage(type.type, to, content, topic)
            .map { it.id }
            .doOnError { error ->
                Log.e("sendMessage", "${error.message}")
            }


    override fun addEmoji(messageId: Int, emojiName: String): Completable =
        service.addEmojiReaction(messageId, name = emojiName).ignoreElement()
            .subscribeOn(Schedulers.io())
            .doOnError { error ->
                Log.e("addEmoji", "${error.message}")
            }


    override fun deleteEmoji(messageId: Int, emojiName: String): Completable =
        service.deleteEmojiReaction(messageId, name = emojiName).ignoreElement()
            .subscribeOn(Schedulers.io())
            .doOnError { error ->
                Log.e("deleteEmoji", "${error.message}")
            }

    private fun MessageResponse.toMessage(
        reactions: List<Reaction> = emptyList(),
        streamId: Int,
        topicTitle: String
    ): Message =
        Message(
            id = id,
            content = content,
            userId = userId,
            isMine = isMine,
            senderName = senderName,
            timestamp = timestamp,
            avatarUrl = avatarUrl,
            reactions = reactions,
            streamId = streamId,
            topicTitle = topicTitle
        )

    private fun MessageResponse.toMessage(
        reactions: List<Reaction> = emptyList(),
        userEmail: String
    ): Message =
        Message(
            id = id,
            content = content,
            userId = userId,
            isMine = isMine,
            senderName = senderName,
            timestamp = timestamp,
            avatarUrl = avatarUrl,
            reactions = reactions,
            userEmail = userEmail
        )

    private fun ReactionResponse.toReaction(): Reaction = Reaction(
        userId = userId,
        code = code,
        name = name
    )

    private fun StreamResponse.toStream(topics: List<Topic>, isSubscribed: Boolean): Stream =
        Stream(
            id = id,
            title = title,
            topics = topics,
            isSubscribed = isSubscribed
        )

    private fun Stream.toStream(topics: List<Topic>): Stream =
        Stream(
            id = id,
            title = title,
            topics = topics,
            isSubscribed = isSubscribed
        )

    private fun TopicResponse.toTopic(streamId: Int): Topic = Topic(
        title = title,
        messageCount = messageCount,
        streamId = streamId
    )

    private fun List<TopicResponse>.toTopics(streamId: Int): List<Topic> =
        map { it.toTopic(streamId) }


    private fun UserResponse.toUser(status: UserStatus = UserStatus.Offline): User = User(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        status = status
    )
}