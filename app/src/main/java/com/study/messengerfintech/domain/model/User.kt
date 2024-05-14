package com.study.messengerfintech.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Int,
    val name: String,
    val email: String,
    val avatarUrl: String,
    var status: UserStatus
) {
    companion object {
        private val DEFAULT: User = User(
            0,
            "",
            "",
            "",
            UserStatus.Offline
        )

        var ME = DEFAULT
    }
}

enum class UserStatus {
    Online,
    Offline,
    Idle;

    companion object {
        fun stringToStatus(string: String): UserStatus =
            when (string) {
                "active" -> Online
                "idle" -> Idle
                else -> Offline
            }
    }
}