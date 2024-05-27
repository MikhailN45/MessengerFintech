package com.study.messengerfintech.utils

sealed interface OnEmojiClickEvent {
    data class EmojiAdd(
        val messageId: Int,
        val emojiName: String
    ) : OnEmojiClickEvent

    data class EmojiDelete(
        val messageId: Int,
        val emojiName: String
    ) : OnEmojiClickEvent
}