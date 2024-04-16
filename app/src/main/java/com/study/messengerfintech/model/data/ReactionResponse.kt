package com.study.messengerfintech.model.data

import com.study.messengerfintech.domain.data.Reaction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReactionResponse(
    @SerialName("user_id") var userId: Int,
    @SerialName("emoji_code") val code: String,
    @SerialName("emoji_name") val name: String,
)

fun ReactionResponse.toReaction(): Reaction = Reaction(
    userId = userId,
    code = code,
    name = name
)