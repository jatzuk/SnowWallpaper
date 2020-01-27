package dev.jatzuk.snowwallpaper.data.preferences

import android.content.Context

class PreferenceLiveData<T : Any>(
    context: Context,
    key: String,
    defaultValue: T
) : AbstractPreferenceLiveData<T>(context, key, defaultValue) {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            else -> throw IllegalArgumentException("illegal argument defaultValue of type ${defaultValue::class.java}")
        }
    }
}
