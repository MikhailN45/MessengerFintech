package com.study.messengerfintech.model.data

data class Reaction(
    val smile: Int,
    var num: Int,
    var userId: String = "me"
)