package com.study.messengerfintech.data.repository

import com.study.messengerfintech.data.database.model.MessageDb
import com.study.messengerfintech.data.database.model.StreamDb
import com.study.messengerfintech.data.database.model.TopicDb
import com.study.messengerfintech.data.database.model.UserDb
import com.study.messengerfintech.data.network.model.MessageResponse
import com.study.messengerfintech.data.network.model.ReactionApi
import com.study.messengerfintech.data.network.model.StreamResponse
import com.study.messengerfintech.data.network.model.TopicResponse
import com.study.messengerfintech.data.network.model.UserResponse
import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.Reaction
import com.study.messengerfintech.domain.model.Stream
import com.study.messengerfintech.domain.model.Topic
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.model.UserStatus

//Stream
fun StreamResponse.toStream(topics: List<Topic>, isSubscribed: Boolean): Stream =
    Stream(
        id = id,
        title = title,
        topics = topics,
        isSubscribed = isSubscribed
    )

fun Stream.toStreamDb(): StreamDb =
    StreamDb(
        title = title,
        id = id,
        topics = topics,
        isSubscribed = isSubscribed
    )

fun StreamDb.toStream(): Stream =
    Stream(
        title = title,
        id = id,
        topics = topics,
        isSubscribed = isSubscribed
    )

fun List<Stream>.toListStreamDb(): List<StreamDb> =
    map { it.toStreamDb() }

fun List<StreamDb>.toListStream(): List<Stream> =
    map { it.toStream() }

//Topic
fun TopicResponse.toTopic(streamId: Int): Topic =
    Topic(
        title = title,
        messageCount = messageCount,
        streamId = streamId
    )

fun List<TopicResponse>.toTopics(streamId: Int): List<Topic> =
    map { it.toTopic(streamId) }

fun Topic.toTopicDb(): TopicDb =
    TopicDb(
        title = title,
        messageCount = messageCount,
        streamId = streamId
    )

fun TopicDb.toTopic(): Topic =
    Topic(
        title = title,
        messageCount = messageCount,
        streamId = streamId
    )

fun List<Topic>.toListTopicDb(): List<TopicDb> =
    map { it.toTopicDb() }

fun List<TopicDb>.toListTopic(): List<Topic> =
    map { it.toTopic() }

//User
fun UserResponse.toUser(status: UserStatus = UserStatus.Offline): User =
    User(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        status = status
    )

fun User.toUserDb(): UserDb =
    UserDb(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        status = status
    )

fun UserDb.toUser(): User =
    User(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        status = status
    )

fun List<User>.toListUserDb(): List<UserDb> =
    map { it.toUserDb() }

fun List<UserDb>.toListUser(): List<User> =
    map { it.toUser() }

//Message
fun MessageResponse.toMessage(
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

fun MessageResponse.toMessage(
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

fun Message.toMessageDb(): MessageDb =
    MessageDb(
        id,
        content,
        userId,
        isMine,
        senderName,
        timestamp,
        avatarUrl,
        reactions,
        userEmail,
        streamId,
        topicTitle
    )

fun MessageDb.toMessage(): Message =
    Message(
        id,
        content,
        userId,
        isMine,
        senderName,
        timestamp,
        avatarUrl,
        reactions,
        userEmail,
        streamId,
        topicTitle
    )

fun List<Message>.toListMessageDb(): List<MessageDb> =
    map { it.toMessageDb() }

fun List<MessageDb>.toListMessage(): List<Message> =
    map { it.toMessage() }

//Reaction
fun ReactionApi.toReaction(): Reaction = Reaction(
    userId = userId,
    code = code,
    name = name
)