package com.study.messengerfintech.domain.model

import java.util.Date

data class Message(
    val id: Int,
    val content: String,
    val userId: Int,
    private val isMine: Boolean = false,
    val senderName: String = "",
    val timestamp: Int = (Date().time / 1000).toInt(),
    val avatarUrl: String = "",
    private val reactions: List<Reaction> = listOf()
) {
    val isFromMe = userId == User.ME.id
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