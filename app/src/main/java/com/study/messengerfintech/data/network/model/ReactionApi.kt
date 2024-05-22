package com.study.messengerfintech.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReactionApi(
    @SerialName("user_id") val userId: Int,
    @SerialName("emoji_code") val code: String,
    @SerialName("emoji_name") val name: String,
)