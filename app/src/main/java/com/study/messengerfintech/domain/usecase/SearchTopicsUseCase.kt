package com.study.messengerfintech.domain.usecase

import com.study.messengerfintech.domain.model.Stream
import com.study.messengerfintech.domain.model.StreamTopicItem
import com.study.messengerfintech.domain.model.TopicItem
import com.study.messengerfintech.domain.model.toStreamItem
import com.study.messengerfintech.domain.model.toStreamItems
import com.study.messengerfintech.domain.model.toTopicItems
import javax.inject.Inject

interface SearchTopicsUseCase {
    operator fun invoke(
        searchQuery: String,
        streams: List<Stream>
    ): List<StreamTopicItem>
}

class SearchTopicsUseCaseImpl @Inject constructor() : SearchTopicsUseCase {
    override fun invoke(
        searchQuery: String,
        streams: List<Stream>
    ): List<StreamTopicItem> = if (searchQuery.isEmpty()) streams.toStreamItems()
    else streams.getStreamAndTopicItemsByQuery(searchQuery)

    private fun List<Stream>.getStreamAndTopicItemsByQuery(query: String): List<StreamTopicItem> =
        map { stream ->
            val queryTopicItems = stream.getTopicItemsByQuery(query)
            Pair(stream, queryTopicItems)
        }.filter { (stream, queryTopicItems) ->
            val isStreamTitleContainsQuery = stream.title.contains(query, ignoreCase = true)
            queryTopicItems.isNotEmpty() || isStreamTitleContainsQuery
        }.fold(mutableListOf()) { streamTopicItems, (stream, queryTopicItems) ->
            val streamItem = stream.toStreamItem()
            if (queryTopicItems.isNotEmpty()) streamItem.isExpanded = true
            streamTopicItems.apply {
                add(streamItem)
                addAll(queryTopicItems)
            }
        }

    private fun Stream.getTopicItemsByQuery(query: String): List<TopicItem> = topics
        .filter { topic -> topic.title.contains(query, ignoreCase = true) }
        .toTopicItems(id)
}