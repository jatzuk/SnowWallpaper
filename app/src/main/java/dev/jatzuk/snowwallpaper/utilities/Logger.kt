package dev.jatzuk.snowwallpaper.utilities

import android.util.Log

object Logger {

    private const val TAG = "Logger"
    var isLogging = true

    fun logging(message: String, tag: String = TAG, logLevel: Int = Log.DEBUG) {
        lowLevelLog(message, tag, logLevel)
    }

    fun errorLog(message: String, tag: String = TAG, e: Throwable? = null) {
        Log.e(tag, message, e)
    }

    private fun lowLevelLog(message: String, tag: String, logLevel: Int) {
        if (isLogging) Log.println(logLevel, tag, message)
    }
}
