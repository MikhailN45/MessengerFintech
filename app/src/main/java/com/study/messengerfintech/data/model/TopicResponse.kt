package com.study.messengerfintech.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopicResponse(
    @SerialName("name") val title: String,
    val messageCount: Int = 0
)

@Serializable
data class TopicsRootResponse (
    val result: String,
    val msg: String,
    val topics: List<TopicResponse>
)