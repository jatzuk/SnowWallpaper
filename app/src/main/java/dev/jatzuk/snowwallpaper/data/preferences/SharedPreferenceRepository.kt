package dev.jatzuk.snowwallpaper.data.preferences

import android.content.Context

class SharedPreferenceRepository private constructor(context: Context) : PreferenceHelper {

    private val backgroundImagePreference =
        SharedPreferenceLiveData(context, PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED, false)

    override fun isSnowfallEnabled(): Boolean {
        return true
    }

    override fun getSnowfallLimit(): Int {
        return 0
    }

    override fun getVelocityFactor(): Int {
        return 0
    }

    override fun isUniqueRadiusEnabled(): Boolean {
        return true
    }

    override fun getMinRadius(): Int {
        return 0
    }

    override fun getMaxRadius(): Int {
        return 0
    }

    override fun isBackgroundImageEnabled(): Boolean {
        return backgroundImagePreference.getValue(PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED, false)
    }

    override fun setBackgroundImageEnabled(isEnabled: Boolean) {
        backgroundImagePreference.saveValue(PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED, isEnabled)
    }

    fun getBackgroundImageSharedPreference() = backgroundImagePreference

    companion object {
        @Volatile
        private var instance: SharedPreferenceRepository? = null

        const val PREF_KEY_IS_SNOWFALL_ENABLED = "PREF_KEY_IS_SNOWFALL_ENABLED"
        const val PREF_KEY_SNOWFALL_LIMIT = "PREF_KEY_SNOWFALL_LIMIT"
        const val PREF_KEY_SNOWFALL_VELOCITY_FACTOR = "PREF_KEY_SNOWFALL_VELOCITY_FACTOR"
        const val PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED =
            "PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED"
        const val PREF_KEY_SNOWFALL_MIN_RADIUS = "PREF_KEY_BACKGROUND_SNOWFLAKES_MIN_RADIUS"
        const val PREF_KEY_BACKGROUND_SNOWFALL_MAX_RADIUS =
            "PREF_KEY_BACKGROUND_SNOWFLAKES_MAX_RADIUS"
        const val PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED = "PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED"

        fun getInstance(context: Context): SharedPreferenceRepository =
            instance ?: synchronized(this) {
                instance ?: SharedPreferenceRepository(context).also { instance = it }
            }
    }
}
