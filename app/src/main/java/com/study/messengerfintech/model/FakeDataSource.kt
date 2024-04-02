package com.study.messengerfintech.model

import com.study.messengerfintech.model.data.Chat
import com.study.messengerfintech.model.data.Message
import com.study.messengerfintech.model.data.Stream
import com.study.messengerfintech.model.data.User
import com.study.messengerfintech.model.utils.Utils

object FakeDataSource {
    private val streams: MutableList<Stream> = mutableListOf()
    private val users: List<User> = listOf(
        User(0, "Darell Steward"),
        User(1, "Darell Steward"),
        User(2, "Darell Steward"),
        User(3, "Darell Steward"),
        User(4, "Darell Steward"),
        User(5, "Darell Steward"),
        User(6, "Darell Steward"),
        User(7, "Darell Steward"),
        User(7, "Darell Steward"),
        User(8, "Darell Steward"),
        User(9, "Darell Steward"),
        User(10, "Darell Steward")
    )

    init {
        streams.addAll(
            mutableListOf(
                Stream(
                    "#General", true,
                    chats = mutableListOf(
                        Chat("Testing"),
                        Chat("Bruh"),
                        Chat("Lectures"),
                        Chat("Homework")
                    )
                ),
                Stream(
                    "#Development", true,
                    chats = mutableListOf(
                        Chat("Android"),
                        Chat("KMP"),
                        Chat("IOS"),
                        Chat("BackEnd")
                    )
                ),
                Stream(
                    "#Design", true,
                    chats = mutableListOf(
                        Chat("UI/UX"),
                        Chat("Prototype")
                    )
                ),
                Stream(
                    "#PR",
                    chats = mutableListOf(
                        Chat("SMM"),
                        Chat("Targeting"),
                        Chat("Blog")
                    )
                ),
                Stream(
                    "#Recruiting",
                    chats = mutableListOf(
                        Chat("Review"),
                        Chat("Meetings")
                    )
                )
            )
        )

        streams[0].chats[0].messages.addAll(with(User(nickName = "Jake Wharton")) {
            mutableListOf(
                Message(1, this, message = Utils.fakeChat[0]),
                Message(2, this, message = Utils.fakeChat[1]),
                Message(3, this, message = Utils.fakeChat[2]),
                Message(4, this, message = Utils.fakeChat[3]),
                Message(5, this, message = Utils.fakeChat[4]),
                Message(6, this, message = Utils.fakeChat[5]),
                Message(7, this, message = Utils.fakeChat[6])
            )
        })
    }

    fun getUsers(): List<User> = users

    fun getStream(num: Int) = streams[num]

    fun getStreamNames(): List<String> = streams.map { it.title }
}