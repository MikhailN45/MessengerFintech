package com.study.messengerfintech.data.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.study.messengerfintech.data.database.TypeConverter
import com.study.messengerfintech.domain.model.Reaction
import java.util.Date

@Entity(tableName = "messages")
@TypeConverters(TypeConverter::class)
data class MessageDb(
    @PrimaryKey
    val id: Int,
    @ColumnInfo("content")
    val content: String,
    @ColumnInfo("user_id")
    val userId: Int,
    @ColumnInfo("is_mine")
    val isMine: Boolean = false,
    @ColumnInfo("sender_name")
    val senderName: String = "",
    @ColumnInfo("timestamp")
    val timestamp: Int = (Date().time / 1000).toInt(),
    @ColumnInfo("avatar_url")
    val avatarUrl: String = "",
    @ColumnInfo("reactions")
    val reactions: List<Reaction> = listOf(),
    @ColumnInfo(name = "user_email")
    val userEmail: String = "",
    @ColumnInfo(name = "stream_id")
    val streamId: Int = 0,
    @ColumnInfo(name = "topic_title")
    val topicTitle: String = ""
)