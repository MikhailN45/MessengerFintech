package com.study.messengerfintech.data.model.api

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

@Serializable
data class UsersRootResponse(
    val result: String,
    val msg: String,
    val members: List<UserResponse>
)

@Serializable
data class PresenceStatus(
    val status: String,
    val timestamp: Long
)

@Serializable
data class PresenceResponse(
    val result: String,
    val msg: String,
    val presence: Map<String, PresenceStatus>
)