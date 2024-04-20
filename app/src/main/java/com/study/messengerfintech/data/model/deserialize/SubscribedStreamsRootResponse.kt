package com.study.messengerfintech.data.model.deserialize

import com.study.messengerfintech.data.model.StreamResponse
import kotlinx.serialization.Serializable

@Serializable
data class SubscribedStreamsRootResponse (
    val result: String,
    val msg: String,
    val subscriptions: List<StreamResponse>
)