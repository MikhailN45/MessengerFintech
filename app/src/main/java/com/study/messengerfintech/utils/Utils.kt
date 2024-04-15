package com.study.messengerfintech.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue

object Utils {
    fun Float.sp(context: Context) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics
    )

    fun Float.toPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

    val colors = arrayOf(
        Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.WHITE
    )

    val fakeChat: List<String> = listOf(
        "Прежде всего, сосредоточьтесь на качестве вашего кода. Чистый и понятный код - это основа успешного приложения.",
        "Не забывайте о тестировании. Unit-тесты и инструментальные тесты помогут вам убедиться, что ваше приложение работает правильно.",
        "Используйте принципы SOLID для написания модульного и легко поддерживаемого кода.",
        "Помните о производительности. Избегайте утечек памяти и неэффективного использования ресурсов.",
        "Следите за новыми технологиями и библиотеками. Например, Jetpack Compose - это большой шаг вперед в разработке пользовательского интерфейса на Android.",
        "Наконец, помните, что разработка - это командная работа. Умение работать в команде и общаться с коллегами так же важно, как и умение писать код.",
        "Желаю вам успехов в разработке и становлении на карьерном пути!"
    )
}