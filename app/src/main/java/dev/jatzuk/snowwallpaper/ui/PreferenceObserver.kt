package dev.jatzuk.snowwallpaper.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import dev.jatzuk.snowwallpaper.utilities.Logger.logging

class PreferenceObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        logging("onResume lifecycle", TAG)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        logging("onPause lifecycle", TAG)
    }

    companion object {
        private const val TAG = "PreferenceObserver"
    }
}
