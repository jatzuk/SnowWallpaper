package dev.jatzuk.snowwallpaper.data.preferences

import android.content.Context

class PreferenceRepository private constructor(context: Context) {

    val snowfallPreference =
        PreferenceLiveData(context, PREF_KEY_IS_SNOWFALL_ENABLED, true)

    val snowfallLimitPreference =
        PreferenceLiveData(context, PREF_KEY_SNOWFALL_LIMIT, 100)

    val snowfallVelocityPreference =
        PreferenceLiveData(context, PREF_KEY_SNOWFALL_VELOCITY_FACTOR, 3)

    val snowfallUniqueRadiusPreference =
        PreferenceLiveData(context, PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED, true)

    val snowfallMinRadiusPreference =
        PreferenceLiveData(context, PREF_KEY_SNOWFALL_MIN_RADIUS, 15)

    val snowfallMaxRadiusPreference =
        PreferenceLiveData(context, PREF_KEY_BACKGROUND_SNOWFALL_MAX_RADIUS, 35)

    val backgroundImagePreference =
        PreferenceLiveData(context, PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED, false)

    companion object {
        @Volatile
        private var instance: PreferenceRepository? = null

        const val PREF_KEY_IS_SNOWFALL_ENABLED =
            "PREF_KEY_IS_SNOWFALL_ENABLED"

        const val PREF_KEY_SNOWFALL_LIMIT =
            "PREF_KEY_SNOWFALL_LIMIT"

        const val PREF_KEY_SNOWFALL_VELOCITY_FACTOR =
            "PREF_KEY_SNOWFALL_VELOCITY_FACTOR"

        const val PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED =
            "PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED"

        const val PREF_KEY_SNOWFALL_MIN_RADIUS =
            "PREF_KEY_BACKGROUND_SNOWFLAKES_MIN_RADIUS"

        const val PREF_KEY_BACKGROUND_SNOWFALL_MAX_RADIUS =
            "PREF_KEY_BACKGROUND_SNOWFLAKES_MAX_RADIUS"

        const val PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED =
            "PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED"

        fun getInstance(context: Context): PreferenceRepository =
            instance ?: synchronized(this) {
                instance ?: PreferenceRepository(context).also { instance = it }
            }
    }
}
