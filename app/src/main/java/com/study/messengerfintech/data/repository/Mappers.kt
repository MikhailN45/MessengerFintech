package com.study.messengerfintech.data.repository

import com.study.messengerfintech.data.database.dto.MessageDto
import com.study.messengerfintech.data.database.dto.StreamDto
import com.study.messengerfintech.data.database.dto.TopicDto
import com.study.messengerfintech.data.database.dto.UserDto
import com.study.messengerfintech.data.model.MessageResponse
import com.study.messengerfintech.data.model.ReactionResponse
import com.study.messengerfintech.data.model.StreamResponse
import com.study.messengerfintech.data.model.TopicResponse
import com.study.messengerfintech.data.model.UserResponse
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

fun Stream.toStream(topics: List<Topic>): Stream =
    Stream(
        id = id,
        title = title,
        topics = topics,
        isSubscribed = isSubscribed
    )

fun Stream.toStreamDto(): StreamDto =
    StreamDto(
        title = title,
        id = id,
        topics = topics,
        isSubscribed = isSubscribed
    )

fun StreamDto.toStream(): Stream =
    Stream(
        title = title,
        id = id,
        topics = topics,
        isSubscribed = isSubscribed
    )

fun List<Stream>.toListStreamDto(): List<StreamDto> =
    map { it.toStreamDto() }

fun List<StreamDto>.toListStream(): List<Stream> =
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

fun Topic.toTopicDto(): TopicDto =
    TopicDto(
        title = title,
        messageCount = messageCount,
        streamId = streamId
    )

fun TopicDto.toTopic(): Topic =
    Topic(
        title = title,
        messageCount = messageCount,
        streamId = streamId
    )

fun List<Topic>.toListTopicDto(): List<TopicDto> =
    map { it.toTopicDto() }

fun List<TopicDto>.toListTopic(): List<Topic> =
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

fun User.toUserDto(): UserDto =
    UserDto(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        status = status
    )

fun UserDto.toUser(): User =
    User(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        status = status
    )

fun List<User>.toListUserDto(): List<UserDto> =
    map { it.toUserDto() }

fun List<UserDto>.toListUser(): List<User> =
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

fun Message.toMessageDto(): MessageDto =
    MessageDto(
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

fun MessageDto.toMessage(): Message =
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

fun List<Message>.toListMessageDto(): List<MessageDto> =
    map { it.toMessageDto() }

fun List<MessageDto>.toListMessage(): List<Message> =
    map { it.toMessage() }

//Reaction
fun ReactionResponse.toReaction(): Reaction = Reaction(
    userId = userId,
    code = code,
    name = name
)