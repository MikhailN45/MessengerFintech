package com.study.messengerfintech.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.study.messengerfintech.data.database.TypeConverter
import java.util.Date

@Entity(tableName = "messages")
@TypeConverters(TypeConverter::class)
data class Message(
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
    val topicTitle: String = "",
) {
    @Ignore
    val isFromMe = userId == User.ME.id
    @Ignore
    val emojiCodeReactionMap: HashMap<String, UnitedReaction> = HashMap()

    init {
        for (reaction in reactions)
            addEmoji(reaction)
    }

    fun addEmoji(reaction: Reaction) {
        val code = reaction.getUnicode()
        if (code in emojiCodeReactionMap) {
            emojiCodeReactionMap[code]?.usersId?.add(reaction.userId)
        } else {
            emojiCodeReactionMap[code] = UnitedReaction(
                mutableListOf(reaction.userId),
                reaction.getUnicode(),
                reaction.name
            )
        }
    }
}