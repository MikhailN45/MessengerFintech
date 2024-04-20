package com.study.messengerfintech.data.model.deserialize

import com.study.messengerfintech.data.model.MessageResponse
import kotlinx.serialization.Serializable

@Serializable
data class MessagesReceiveResponse(
    val result: String,
    val msg: String,
    val messages: List<MessageResponse>
)
