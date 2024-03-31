package com.study.messengerfintech.model.data

data class Chat(
    val title: String,
    val messages: MutableList<Message> = mutableListOf()
)