package com.study.messengerfintech.model.data

import com.study.messengerfintech.domain.model.Topic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopicResponse(
    @SerialName("name") val title: String,
    @SerialName("max_id") val lastMesID: Int = 0,
)

fun TopicResponse.toTopic(): Topic = Topic(
    title = title,
    lastMesID = lastMesID
)

fun List<TopicResponse>.toTopics(): List<Topic> = map { it.toTopic() }