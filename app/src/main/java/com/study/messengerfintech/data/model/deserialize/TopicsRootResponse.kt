package com.study.messengerfintech.data.model.deserialize

import com.study.messengerfintech.data.model.TopicResponse
import kotlinx.serialization.Serializable

@Serializable
data class TopicsRootResponse (
    val result: String,
    val msg: String,
    val topics: List<TopicResponse>
)