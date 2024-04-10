package com.study.messengerfintech.model.data

data class User(
    val id: Int = 1,
    val nickName: String
) {
    companion object {
        val INSTANCE by lazy {
            User(id = 0, nickName = "me")
        }
    }
}