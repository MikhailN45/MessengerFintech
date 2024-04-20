package com.study.messengerfintech.data.model.deserialize

import com.study.messengerfintech.data.model.StreamResponse
import kotlinx.serialization.Serializable

@Serializable
data class AllStreamRootResponse(
    val result: String,
    val msg: String,
    val streams: List<StreamResponse>
)