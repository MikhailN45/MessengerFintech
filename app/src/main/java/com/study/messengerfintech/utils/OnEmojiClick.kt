package com.study.messengerfintech.utils

sealed class OnEmojiClick(val id: Int, open val name: String)

data class EmojiAdd(
    val messageId: Int,
    val emojiName: String
) : OnEmojiClick(messageId, emojiName)

data class EmojiDelete(
    val messageId: Int,
    val emojiName: String
) : OnEmojiClick(messageId, emojiName)