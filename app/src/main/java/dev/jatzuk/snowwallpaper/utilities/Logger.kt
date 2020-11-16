package dev.jatzuk.snowwallpaper.utilities

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object Logger {

    private const val TAG = "Logger"
    var isLogging = true

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun d(
        message: String,
        tag: String = TAG,
        level: Int = Log.DEBUG,
        sendToFBA: Boolean = true
    ) {
        lowLevelLog(message, tag, level)
        if (sendToFBA) firebaseLog(message, tag)
    }

    fun e(
        message: String,
        tag: String = TAG,
        e: Throwable? = null,
        sendToFB: Boolean = true
    ) {
        Log.e(tag, message, e)
        if (sendToFB) firebaseLog(message, tag)
    }

    private fun lowLevelLog(message: String, tag: String, logLevel: Int) {
        if (isLogging) Log.println(logLevel, tag, message)
    }

    private fun firebaseLog(message: String, tag: String) {
        val bundle = Bundle().apply { putString(tag, message) }
        firebaseAnalytics.logEvent(tag, bundle)
    }

    fun initFirebaseAnalytics() {
        firebaseAnalytics = Firebase.analytics
    }
}
