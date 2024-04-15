package com.study.messengerfintech.model.data

sealed class StreamAndChatItem

data class StreamItem(
    val id: Int,
    val title: String,
    val isSubscribed: Boolean = false,
    var isExpanded: Boolean = false,
    var isLoading: Boolean = false,
    val chats: List<ChatItem>
) : StreamAndChatItem()

data class ChatItem(
    val title: String,
    val messageCount: Int,
    val streamId: Int,
    val chatId: Int
) : StreamAndChatItem()