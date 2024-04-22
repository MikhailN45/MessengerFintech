package com.study.messengerfintech.data.model

import com.study.messengerfintech.domain.model.Reaction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReactionResponse(
    @SerialName("user_id") val userId: Int,
    @SerialName("emoji_code") val code: String,
    @SerialName("emoji_name") val name: String,
)

fun ReactionResponse.toReaction(): Reaction = Reaction(
    userId = userId,
    code = code,
    name = name
)