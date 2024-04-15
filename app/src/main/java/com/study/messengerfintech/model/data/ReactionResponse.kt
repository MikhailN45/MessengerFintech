package com.study.messengerfintech.model.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReactionResponse(
    @SerialName("user_id") var userId: Int,
    @SerialName("emoji_code") val code: String,
    @SerialName("emoji_name") val name: String,
)