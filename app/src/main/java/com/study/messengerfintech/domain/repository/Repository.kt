package com.study.messengerfintech.domain.repository

import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.Stream
import com.study.messengerfintech.domain.model.Topic
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.utils.SendType
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface Repository {
    fun requestAllStreams(): Completable

    fun requestSubscribedStreams(): Completable

    fun getAllStreams(): Observable<List<Stream>>

    fun getSubscribedStreams(): Observable<List<Stream>>

    fun getMessageCountForTopic(stream: Int, topic: String): Single<Int>

    fun loadTopics(streamId: Int): Single<List<Topic>>

    fun loadTopicMessages(stream: Int, topic: String): Single<List<Message>>

    fun loadPrivateMessages(userEmail: String): Single<List<Message>>

    fun sendMessage(
        type: SendType,
        to: String, content: String,
        topic: String = ""
    ): Single<Int>

    fun loadOwnUser(): Single<User>

    fun loadStatus(user: User): Single<User>

    fun loadUsers(): Observable<List<User>>

    fun addEmoji(messageId: Int, emojiName: String): Completable

    fun deleteEmoji(messageId: Int, emojiName: String): Completable
}