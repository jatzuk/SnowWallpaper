package dev.jatzuk.snowwallpaper.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager

abstract class AbstractPreferenceLiveData<T : Any>(
    context: Context,
    private val key: String,
    private val defaultValue: T
) : LiveData<T>() {

    protected val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    private val sharedPreferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == this.key) value = getValue(key, defaultValue)
        }

    abstract fun getValue(key: String, defaultValue: T): T

    override fun onActive() {
        super.onActive()
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceListener)
        value = getValue(key, defaultValue)
    }

    override fun onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceListener)
        super.onInactive()
    }
}
