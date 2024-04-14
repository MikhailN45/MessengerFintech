package com.study.messengerfintech.model.data

import com.study.messengerfintech.domain.model.Stream
import com.study.messengerfintech.domain.model.Topic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamResponse(
    @SerialName("name") val title: String,
    @SerialName("stream_id") val id: Int,
)

fun StreamResponse.toStream(topics: List<Topic>): Stream = Stream(
    id = id,
    title = title,
    topics = topics
)