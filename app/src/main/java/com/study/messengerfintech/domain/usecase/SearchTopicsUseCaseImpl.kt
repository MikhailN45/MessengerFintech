package com.study.messengerfintech.domain.usecase

import com.study.messengerfintech.domain.mapper.StreamToItemMapper
import com.study.messengerfintech.domain.mapper.TopicToItemMapper
import com.study.messengerfintech.domain.model.Stream
import com.study.messengerfintech.model.data.StreamTopicItem
import com.study.messengerfintech.model.data.TopicItem
import io.reactivex.Observable
import io.reactivex.Single

interface SearchTopicsUseCase :
        (String, Single<List<Stream>>) -> Observable<List<StreamTopicItem>> {
    override fun invoke(
        searchQuery: String,
        streams: Single<List<Stream>>
    ): Observable<List<StreamTopicItem>>
}

internal class SearchTopicsUseCaseImpl : SearchTopicsUseCase {
    private val streamToItemMapper: StreamToItemMapper = StreamToItemMapper()
    private val topicToItemMapper: TopicToItemMapper = TopicToItemMapper()

    override fun invoke(
        searchQuery: String,
        streams: Single<List<Stream>>
    ): Observable<List<StreamTopicItem>> {
        return streams.map { topics ->
            if (searchQuery.isNotEmpty())
                topics.search(searchQuery)
            else
                topics.map(streamToItemMapper)
        }.toObservable()
    }

    private fun List<Stream>.search(query: String): List<StreamTopicItem> {
        val items = mutableListOf<StreamTopicItem>()
        this.forEach { stream ->
            val topics = stream.search(query)
            if (topics.isNotEmpty() || stream.title.contains(query, ignoreCase = true)) {
                items.add(streamToItemMapper(stream).apply {
                    if (topics.isNotEmpty())
                        isExpanded = true
                })
                items.addAll(topics)
            }
        }
        return items
    }

    private fun Stream.search(query: String): List<TopicItem> {
        val correctChats =
            topics.filter { topic -> topic.title.contains(query, ignoreCase = true) }
        return topicToItemMapper(correctChats, this.id)
    }
}