package com.study.messengerfintech.domain.usecase

import com.study.messengerfintech.domain.model.Stream
import com.study.messengerfintech.domain.model.StreamTopicItem
import com.study.messengerfintech.domain.model.TopicItem
import com.study.messengerfintech.domain.model.toStreamItem
import com.study.messengerfintech.domain.model.toTopicItem
import io.reactivex.Single

interface SearchTopicsUseCase {
    operator fun invoke(
        searchQuery: String,
        streams: Single<List<Stream>>
    ): Single<List<StreamTopicItem>>
}

class SearchTopicsUseCaseImpl : SearchTopicsUseCase {
    override fun invoke(
        searchQuery: String,
        streams: Single<List<Stream>>
    ): Single<List<StreamTopicItem>> {
        //Если есть запрос, выполняем поиск, если нет просто возврашаем streamItem
        return streams.map { streamList ->
            if (searchQuery.isNotEmpty()) streamList.searchQueryInStreams(searchQuery)
            else streamList.map { stream ->
                stream.toStreamItem()
            }
        }
    }

    //для каждого стрима ищем топики, если они есть раскрываем стрим, конвертируя его в streamItem
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

    //Возвращаем по стриму топики которые содержат запрос в названии и конвертируем их в topicItem
    private fun Stream.searchQueryInTopics(query: String): List<TopicItem> {
        val correctChats =
            topics.filter { topic -> topic.title.contains(query, ignoreCase = true) }
        return toTopicItem(correctChats, id)
    }
}