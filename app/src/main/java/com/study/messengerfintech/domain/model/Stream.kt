package com.study.messengerfintech.domain.model

data class Stream(
    val title: String,
    val id: Int,
    val topics: List<Topic>
)
