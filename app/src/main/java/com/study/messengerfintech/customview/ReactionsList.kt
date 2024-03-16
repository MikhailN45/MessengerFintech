package com.study.messengerfintech.customview

enum class ReactionsList(private val unicode: String) {
    SMILING("\uD83D\uDE00"),
    WINKING("\uD83D\uDE09"),
    HEART("\uD83D\uDE0D"),
    SAD("\uD83D\uDE22"),
    JOY("\uD83D\uDE1D"),
    NOT_INTERESTED("\uD83D\uDE11"),
    HUG("\uD83E\uDD17"),
    PARTY("\uD83E\uDD73"),
    CRY("\uD83D\uDE2D");

    override fun toString(): String {
        return unicode
    }
}