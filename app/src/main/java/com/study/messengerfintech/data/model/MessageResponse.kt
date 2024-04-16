package com.study.messengerfintech.data.model

import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.Reaction
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
    @SerialName("reactions") val reactions: List<ReactionResponse> = listOf()
)

fun MessageResponse.toMessage(reactions: List<Reaction> = emptyList()): Message = Message(
    id = id,
    content = content,
    userId = userId,
    isMine = isMine,
    senderName = senderName,
    timestamp = timestamp,
    avatarUrl = avatarUrl,
    reactions = reactions
)