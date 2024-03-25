package com.study.messengerfintech.data

data class Reaction(
    var isMine: Boolean,
    val smile: Int,
    var num: Int,
    var userId: Int
)