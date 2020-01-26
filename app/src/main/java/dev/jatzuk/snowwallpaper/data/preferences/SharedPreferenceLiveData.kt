package dev.jatzuk.snowwallpaper.data.preferences

import android.content.Context

class SharedPreferenceLiveData<T>(
    context: Context,
    key: String,
    defaultValue: T
) : AbstractSharedPreferenceLiveData<T>(context, key, defaultValue) {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(key: String, defaultValue: T): T {
        return when(defaultValue) {
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            else -> throw TypeCastException("unsupported shared preference type")
        }
    }

    fun saveValue(key: String, value: T) {
        super.putValue(key, value)
    }
}
