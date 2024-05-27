package com.study.messengerfintech.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.study.messengerfintech.domain.model.UserStatus

@Entity(tableName = "users")
data class UserDb(
    @PrimaryKey
    val id: Int,
    val name: String,
    val email: String,
    val avatarUrl: String,
    var status: UserStatus
)