package com.study.messengerfintech.utils

import androidx.test.platform.app.InstrumentationRegistry
import java.io.BufferedReader
import java.io.InputStreamReader

object AssetsUtils {
    fun fromAssets(path: String, vararg formatArgs: Any? = emptyArray()): String {
        val assetManager = InstrumentationRegistry.getInstrumentation().context.assets
        val stringFile =
            BufferedReader(InputStreamReader(assetManager.open(path), Charsets.UTF_8)).readText()
        return if (formatArgs.isNotEmpty()) stringFile.format(*formatArgs)
        else stringFile
    }
}