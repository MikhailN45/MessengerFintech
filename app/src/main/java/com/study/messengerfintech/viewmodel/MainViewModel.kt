package com.study.messengerfintech.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.study.messengerfintech.model.FakeDataSource
import com.study.messengerfintech.model.data.Chat
import com.study.messengerfintech.model.data.ChatItem

class MainViewModel : ViewModel() {
    private val dataSource = FakeDataSource
    val chat = MediatorLiveData<Pair<Int, Int>>()

    fun getStreams(subscribed: Boolean = false): List<Pair<Int, String>> =
        dataSource.getStreamNames().mapIndexed { index, title ->
            index to title
        }.filter {
            !subscribed || dataSource.getStream(it.first).isSubscribed
        }

    fun getChat(streamId: Int, chatId: Int): Chat = dataSource.getStream(streamId).chats[chatId]

    fun openChat(streamId: Int, chatId: Int) = chat.postValue(streamId to chatId)

    fun getChatsFromStream(streamCount: Int): List<ChatItem> =
        dataSource.getStream(streamCount).chats.mapIndexed { index, chat ->
            ChatItem(chat.title, chat.messages.size, streamCount, index)
        }
}