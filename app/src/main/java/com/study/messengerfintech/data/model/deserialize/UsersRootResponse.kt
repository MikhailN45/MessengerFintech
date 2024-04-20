package com.study.messengerfintech.data.model.deserialize

import com.study.messengerfintech.data.model.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class UsersRootResponse(
    val result: String,
    val msg: String,
    val members: List<UserResponse>
)