package com.study.messengerfintech.model.data

data class Stream(
    val title: String,
    val id: Int,
    val isSubscribed: Boolean = false,
    val chats: MutableList<Chat> = mutableListOf()
)