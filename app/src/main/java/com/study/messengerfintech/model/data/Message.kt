package com.study.messengerfintech.model.data

data class Message(
    val id: Int,
    var user: User,
    val message: String,
    val reactions: MutableList<Reaction> = mutableListOf()
)