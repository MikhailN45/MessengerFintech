package com.study.messengerfintech.data.network

import kotlinx.serialization.Serializable

@Serializable
sealed interface Narrow

@Serializable
data class NarrowStr(val operator: String, val operand: String) : Narrow

@Serializable
data class NarrowInt(val operator: String, val operand: Int) : Narrow