package com.study.messengerfintech.data.model.deserialize

import kotlinx.serialization.Serializable

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
