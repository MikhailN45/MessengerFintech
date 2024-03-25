package com.study.messengerfintech.data

data class Message(
    val id: Int,
    var isMine: Boolean,
    val message: String,
    val senderNickname: String = "Darrell Steward",
    val reactions: MutableList<Reaction> = mutableListOf()
)