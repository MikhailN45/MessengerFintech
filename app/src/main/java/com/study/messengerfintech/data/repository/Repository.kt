package com.study.messengerfintech.data.repository

import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.Stream
import com.study.messengerfintech.domain.model.Topic
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.model.UserStatus
import io.reactivex.Completable
import io.reactivex.Single

interface Repository {

    fun loadStreams(): Single<List<Stream>>

    fun loadSubscribedStreams(): Single<List<Stream>>

    fun loadTopics(id: Int): Single<List<Topic>>

    fun loadTopicMessages(stream: Int, topic: String): Single<List<Message>>

    fun loadPrivateMessages(userEmail: String): Single<List<Message>>

    fun sendMessage(
        type: RepositoryImpl.SendType,
        to: String, content: String,
        topic: String = ""
    ): Single<Int>

    fun loadOwnUser(): Single<User>

    fun loadStatus(user: User): Single<UserStatus>

    fun loadUsers(): Single<List<User>>

    fun addEmoji(messageId: Int, emojiName: String): Completable

    fun deleteEmoji(messageId: Int, emojiName: String): Completable
}