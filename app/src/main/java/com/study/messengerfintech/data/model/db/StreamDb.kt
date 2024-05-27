package com.study.messengerfintech.data.model.db

import androidx.room.Entity
import androidx.room.TypeConverters
import com.study.messengerfintech.data.database.TypeConverter
import com.study.messengerfintech.domain.model.Topic

@Entity(tableName = "streams", primaryKeys = ["title", "isSubscribed"])
@TypeConverters(TypeConverter::class)
data class StreamDb(
    val title: String,
    val id: Int,
    val topics: List<Topic>,
    val isSubscribed: Boolean = false
)