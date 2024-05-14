package com.study.messengerfintech.domain.model

data class User(
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