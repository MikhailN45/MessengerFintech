package com.study.messengerfintech.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    fun Float.toSp(context: Context) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics
    )

    fun Float.toPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

    fun getDayMonthFromTimestamp(timestamp: Int): String {
        val date = Date(timestamp.toLong() * 1000)
        return SimpleDateFormat("d MMM", Locale("ru", "RU")).format(date)
    }

    fun countDaysInTimestamp(timestamp: Int): Int {
        val secondsInDay = 86400
        return timestamp / secondsInDay
    }

    val emojiNameUnicodeHashMap: HashMap<String, String> = hashMapOf(
        "smile" to "\uD83D\uDE42",
        "heart" to "❤",
        "grinning" to "\uD83D\uDE00",
        "working_on_it" to "\uD83D\uDEE0",
        "thinking" to "\uD83E\uDD14",
        "sunglasses" to "\uD83D\uDE0E",
        "hug" to "\uD83E\uDD17",
        "fear" to "\uD83D\uDE28",
        "scream" to "\uD83D\uDE31",
        "nerd" to "\uD83E\uDD13",
        "tada" to "\uD83C\uDF89",
        "+1" to "\uD83D\uDC4D",
        "expressionless" to "\uD83D\uDE11",
        "octopus" to "\uD83D\uDC19",
        "stuck_out_tongue_closed_eyes" to "\uD83D\uDE1D",
        "silence" to "\uD83E\uDD10",
        "money_face" to "\uD83E\uDD11",
        "smirk" to "\uD83D\uDE12",
        "poop" to "\uD83D\uDCA9",
        "rolling_eyes" to "\uD83D\uDE44",
        "rage" to "\uD83D\uDE21",
        "flushed" to "\uD83D\uDE33",
        "ghost" to "\uD83D\uDC7B",
        "pensive" to "\uD83D\uDE14",
        "jack_o_lantern" to "\uD83C\uDF83",
        "nauseated" to "\uD83E\uDD22",
        "alien" to "\uD83D\uDC7D",
        "middle_finger" to "\uD83D\uDD95",
    )
}