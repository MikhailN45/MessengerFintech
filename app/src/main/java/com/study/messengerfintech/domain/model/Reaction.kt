package com.study.messengerfintech.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Reaction(
    val userId: Int,
    val code: String,
    val name: String,
) {
    fun getUnicode() = processUnicode(code)
}

data class UnitedReaction(
    val usersId: MutableList<Int>,
    private val code: String,
    val name: String
) {
    fun getUnicode() = processUnicode(code)
}

private fun processUnicode(code: String): String =
    try {
        String(Character.toChars(code.toInt(16)))
    } catch (e: NumberFormatException) {
        code
    }