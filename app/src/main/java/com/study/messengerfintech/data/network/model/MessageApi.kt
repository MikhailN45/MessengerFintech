package com.study.messengerfintech.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class MessageResponse(
    @SerialName("id") val id: Int,
    @SerialName("content") val content: String,
    @SerialName("sender_id") val userId: Int,
    @SerialName("is_me_message") val isMine: Boolean = false,
    @SerialName("sender_full_name") val senderName: String = "",
    @SerialName("timestamp") val timestamp: Int = (Date().time / 1000).toInt(),
    @SerialName("avatar_url") val avatarUrl: String = "",
    @SerialName("reactions") val reactions: List<ReactionApi> = listOf()
)

@Serializable
data class MessageSendResponse(
    val id: Int
)

@Serializable
data class MessagesReceiveResponse(
    val result: String,
    val msg: String,
    val messages: List<MessageResponse>
)