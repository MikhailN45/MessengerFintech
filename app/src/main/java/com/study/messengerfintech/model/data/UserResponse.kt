package com.study.messengerfintech.model.data

import com.study.messengerfintech.domain.data.User
import com.study.messengerfintech.domain.data.UserStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("user_id") val id: Int,
    @SerialName("full_name") val name: String,
    @SerialName("email") val email: String = "",
    @SerialName("avatar_url") val avatarUrl: String = "",
    val status: String = ""
)

fun UserResponse.toUser(status: UserStatus = UserStatus.Offline): User = User(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl,
    status = status
)