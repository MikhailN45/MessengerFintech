package com.study.messengerfintech.model.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
//todo separate data class to domain/network, add mappers
@Serializable
data class Message(
    @SerialName("id") val id: Int,
    @SerialName("content") val content: String,
    @SerialName("sender_id") val userId: Int,
    @SerialName("is_me_message") private val isMine: Boolean = false,
    @SerialName("sender_full_name") val senderName: String = "",
    @SerialName("timestamp") val timestamp: Int = (Date().time / 1000).toInt(),
    @SerialName("avatar_url") val avatarUrl: String = "",
    @SerialName("reactions") private val reactions: List<Reaction> = listOf()
) {
    @kotlinx.serialization.Transient
    val emojiCodeReactionMap: HashMap<String, UnitedReaction> = HashMap()

    @kotlinx.serialization.Transient
    val isFromMe = userId == User.ME.id

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

    fun getDate(): String {
        val date = Date(timestamp.toLong() * 1000)
        return SimpleDateFormat("d MMM", Locale("ru", "RU")).format(date)
    }
}