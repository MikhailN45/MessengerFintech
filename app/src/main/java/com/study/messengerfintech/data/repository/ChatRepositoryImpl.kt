package com.study.messengerfintech.data.repository

import android.util.Log
import com.study.messengerfintech.data.database.AppDatabase
import com.study.messengerfintech.data.model.MessageResponse
import com.study.messengerfintech.data.model.ReactionResponse
import com.study.messengerfintech.data.network.NarrowInt
import com.study.messengerfintech.data.network.NarrowStr
import com.study.messengerfintech.data.network.ZulipApiService
import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.Reaction
import com.study.messengerfintech.domain.repository.ChatRepository
import com.study.messengerfintech.utils.SendType
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.encodeToJsonElement
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val service: ZulipApiService,
    private val database: AppDatabase
) : ChatRepository {

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