package com.study.messengerfintech.domain.usecase

import com.study.messengerfintech.model.source.FakeDataSourceImpl
import com.study.messengerfintech.model.data.Chat
import com.study.messengerfintech.model.data.ChatItem
import com.study.messengerfintech.model.data.Stream
import com.study.messengerfintech.model.data.StreamAndChatItem
import com.study.messengerfintech.domain.mapper.ChatMapper
import com.study.messengerfintech.domain.mapper.StreamMapper
import io.reactivex.Observable

interface SearchStreamUseCase : (String) -> Observable<List<StreamAndChatItem>> {
    override fun invoke(searchRequest: String): Observable<List<StreamAndChatItem>>
}

class SearchStreamChatsUseCaseImpl : SearchStreamUseCase {
    private val dataSource = FakeDataSourceImpl
    private val streamMapper: StreamMapper = StreamMapper()
    private val chatMapper: ChatMapper = ChatMapper()

    override fun invoke(searchRequest: String): Observable<List<StreamAndChatItem>> {
        return dataSource.loadStreams()
            .map { streams ->
                if (searchRequest.isNotEmpty()) streams.search(searchRequest)
                else streams.map(streamMapper)
            }
    }

    private fun List<Stream>.search(request: String): List<StreamAndChatItem> {
        val items = mutableListOf<StreamAndChatItem>()

        this.forEach { stream ->
            val chats = stream.search(request)
            if (chats.isNotEmpty() || stream.title.contains(request, ignoreCase = true)) {
                items.add(streamMapper(stream).apply {
                    if (chats.isNotEmpty()) isExpanded = true
                })
                items.addAll(chats)
            }
        }
        return items
    }

    private fun Stream.search(request: String): List<ChatItem> {
        val correctChats = mutableListOf<Chat>()
        this.chats.forEach { chat ->
            if (chat.title.contains(request, ignoreCase = true)) correctChats.add(chat)
        }
        return chatMapper(correctChats, this.id)
    }
}