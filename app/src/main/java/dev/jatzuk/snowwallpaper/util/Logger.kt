package dev.jatzuk.snowwallpaper.util

import android.util.Log

object Logger {
    private const val TAG = "Logger"
    var isLogging = true

    fun logging(message: String, tag: String = TAG, logLevel: Int = Log.DEBUG) {
        lowLevelLog(message, tag, logLevel)
    }

    fun logWithTag(message: String, tag: String, logLevel: Int = Log.DEBUG) {
        lowLevelLog(message, tag + TAG, logLevel)
    }

    fun errorLog(message: String, tag: String = TAG) {
        lowLevelLog(message, tag, Log.ERROR)
    }

    private fun lowLevelLog(message: String, tag: String, logLevel: Int) {
        if (isLogging) Log.println(logLevel, tag, message)
    }
}
