package dev.jatzuk.snowwallpaper.utilities

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics

object Logger {
    private const val TAG = "Logger"
    var isLogging = true

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun logging(
        message: String,
        tag: String = TAG,
        logLevel: Int = Log.DEBUG,
        translateToFirebase: Boolean = true
    ) {
        lowLevelLog(message, tag, logLevel)
        if (translateToFirebase) firebaseLog(message, tag)
    }

    fun errorLog(
        message: String,
        tag: String = TAG,
        e: Throwable? = null,
        translateToFirebase: Boolean = true
    ) {
        Log.e(tag, message, e)
        if (translateToFirebase) firebaseLog(message, tag)
    }

    private fun lowLevelLog(message: String, tag: String, logLevel: Int) {
        if (isLogging) Log.println(logLevel, tag, message)
    }

    private fun firebaseLog(message: String, tag: String) {
        val bundle = Bundle().apply { putString(tag, message) }
        firebaseAnalytics.logEvent(tag, bundle)
    }

    fun initFirebaseAnalytics(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }
}
