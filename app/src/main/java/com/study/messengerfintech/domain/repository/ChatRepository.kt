package com.study.messengerfintech.domain.repository

import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.utils.SendType
import io.reactivex.Completable
import io.reactivex.Single

interface ChatRepository {
    fun loadTopicMessages(stream: Int, topic: String, anchor: String): Single<List<Message>>

    fun loadPrivateMessages(userEmail: String, anchor: String): Single<List<Message>>

    fun sendMessage(
        type: SendType,
        to: String, content: String,
        topic: String = ""
    ): Single<Int>

    fun addEmoji(messageId: Int, emojiName: String): Completable

    fun deleteEmoji(messageId: Int, emojiName: String): Completable
}