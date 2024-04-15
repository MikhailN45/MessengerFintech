package com.study.messengerfintech.domain.usecase

import com.study.messengerfintech.domain.data.Stream
import com.study.messengerfintech.domain.data.StreamTopicItem
import com.study.messengerfintech.domain.data.TopicItem
import com.study.messengerfintech.domain.data.toStreamItem
import com.study.messengerfintech.domain.data.toTopicItem
import io.reactivex.Observable
import io.reactivex.Single

interface SearchTopicsUseCase :
        (String, Single<List<Stream>>) -> Observable<List<StreamTopicItem>> {
    override fun invoke(
        searchQuery: String,
        streams: Single<List<Stream>>
    ): Observable<List<StreamTopicItem>>
}

class SearchTopicsUseCaseImpl : SearchTopicsUseCase {
    override fun invoke(
        searchQuery: String,
        streams: Single<List<Stream>>
    ): Observable<List<StreamTopicItem>> {
        return streams.map { streamList ->
            if (searchQuery.isNotEmpty()) streamList.searchQueryInStreams(searchQuery)
            else streamList.map { stream ->
                stream.toStreamItem()
            }
        }.toObservable()
    }

    private fun List<Stream>.searchQueryInStreams(query: String): List<StreamTopicItem> {
        val items = mutableListOf<StreamTopicItem>()
        forEach { stream ->
            val topicItemList = stream.searchQueryInTopics(query)

            if (topicItemList.isNotEmpty() || stream.title.contains(query, ignoreCase = true)) {
                items.add(stream.toStreamItem().apply {
                    if (topicItemList.isNotEmpty()) {
                        isExpanded = true
                    }
                })

                items.addAll(topicItemList)
            }
        }
        return items
    }

    private fun Stream.searchQueryInTopics(query: String): List<TopicItem> {
        val correctChats =
            topics.filter { topic -> topic.title.contains(query, ignoreCase = true) }
        return toTopicItem(correctChats, id)
    }
}