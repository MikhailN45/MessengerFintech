package com.study.messengerfintech.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

object Utils {
    fun Float.sp(context: Context) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics
    )

    fun Float.toPx() = (this * Resources.getSystem().displayMetrics.density).toInt()
}