package com.study.messengerfintech.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "topics")
@Serializable
data class Topic(
    @PrimaryKey
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("message_count")
    var messageCount: Int = 0,
    @ColumnInfo("stream_id")
    var streamId: Int = 0
)

fun List<Topic>.toTopicItems(streamId: Int): List<TopicItem> = mapIndexed { index, topic ->
    TopicItem(
        title = topic.title,
        messageCount = topic.messageCount,
        topicId = index,
        streamId = streamId
    )
}