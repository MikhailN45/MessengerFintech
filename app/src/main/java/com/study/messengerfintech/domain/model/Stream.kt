package com.study.messengerfintech.domain.model

data class Stream(
    val title: String,
    val id: Int,
    val topics: List<Topic>
)

fun Stream.toStreamItem(): StreamItem = StreamItem(
    streamId = id,
    title = title,
    isExpanded = false,
    topics = toTopicItem(topics, id)
)

