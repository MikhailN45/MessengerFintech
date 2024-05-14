package com.study.messengerfintech.domain.model

import androidx.room.Entity
import androidx.room.TypeConverters
import com.study.messengerfintech.data.database.TypeConverter

@Entity(tableName = "streams", primaryKeys = ["title", "isSubscribed"])
@TypeConverters(TypeConverter::class)
data class Stream(
    val title: String,
    val id: Int,
    val topics: List<Topic>,
    val isSubscribed: Boolean = false
)

fun Stream.toStreamItem(): StreamItem = StreamItem(
    streamId = id,
    title = title,
    isExpanded = false,
    topics = topics.toTopicItems(id)
)

fun List<Stream>.toStreamItems(): List<StreamItem> = map { it.toStreamItem() }
