package com.study.messengerfintech.data.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamResponse(
    @SerialName("name") val title: String,
    @SerialName("stream_id") val id: Int,
)

@Serializable
data class AllStreamRootResponse(
    val result: String,
    val msg: String,
    val streams: List<StreamResponse>
)

@Serializable
data class SubscribedStreamsRootResponse (
    val result: String,
    val msg: String,
    val subscriptions: List<StreamResponse>
)