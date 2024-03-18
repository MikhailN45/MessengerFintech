package com.study.messengerfintech.utils

import android.content.Context
import android.util.TypedValue

object Utils {
    /** fun convert px to sp for custom views text*/
    fun Float.sp(context: Context) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics
    )
}