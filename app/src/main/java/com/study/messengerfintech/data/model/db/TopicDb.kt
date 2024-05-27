package com.study.messengerfintech.data.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "topics")
@Serializable
data class TopicDb(
    @PrimaryKey
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("message_count")
    var messageCount: Int = 0,
    @ColumnInfo("stream_id")
    val streamId: Int = 0
)