package com.study.messengerfintech.data.database

import androidx.room.TypeConverter
import com.study.messengerfintech.domain.model.Reaction
import com.study.messengerfintech.domain.model.Topic
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TypeConverter {
    private val format = Json { encodeDefaults = true }

    @TypeConverter
    fun fromTopicList(topics: List<Topic>): String {
        return format.encodeToString(topics)
    }

    @TypeConverter
    fun toTopicList(topicsString: String): List<Topic> {
        return format.decodeFromString<List<Topic>>(topicsString)
    }

    @TypeConverter
    fun fromReactionsList(reactions: List<Reaction>): String {
        return format.encodeToString(reactions)
    }

    @TypeConverter
    fun toReactionsList(reactions: String): List<Reaction> {
        return format.decodeFromString<List<Reaction>>(reactions)
    }
}