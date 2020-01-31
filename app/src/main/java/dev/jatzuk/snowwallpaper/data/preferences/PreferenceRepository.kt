package dev.jatzuk.snowwallpaper.data.preferences

import android.content.Context
import androidx.preference.PreferenceManager

class PreferenceRepository private constructor(context: Context) {

    private val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)

    val backgroundImagePreference =
        PreferenceLiveData(
            context,
            PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED,
            IS_BACKGROUND_IMAGE_ENABLED_DEFAULT_VALUE
        )

    fun getIsSnowfallEnabled(): Boolean =
        preferenceManager.getBoolean(
            PREF_KEY_IS_SNOWFALL_ENABLED,
            SNOWFALL_IS_ENABLED_DEFAULT_VALUE
        )

    fun getSnowfallLimit(): Int =
        preferenceManager.getInt(PREF_KEY_SNOWFALL_LIMIT, SNOWFALL_LIMIT_DEFAULT_VALUE)

    fun getSnowfallVelocityFactor(): Int =
        preferenceManager.getInt(
            PREF_KEY_SNOWFALL_VELOCITY_FACTOR,
            SNOWFALL_VELOCITY_FACTOR_DEFAULT_VALUE
        )

    fun getIsSnowfallUniqueRadiusEnabled(): Boolean =
        preferenceManager.getBoolean(
            PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED,
            SNOWFALL_IS_UNIQUE_RADIUS_ENABLED
        )

    fun getSnowfallMinRadius(): Float =
        preferenceManager.getInt(
            PREF_KEY_SNOWFALL_MIN_RADIUS,
            SNOWFALL_MIN_RADIUS_DEFAULT_VALUE
        ).toFloat()

    fun getSnowfallMaxRadius(): Float =
        preferenceManager.getInt(
            PREF_KEY_SNOWFALL_MAX_RADIUS,
            SNOWFALL_MAX_RADIUS_DEFAULT_VALUE
        ).toFloat()

    fun getIsBackgroundImageEnabled(): Boolean =
        preferenceManager.getBoolean(
            PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED,
            IS_BACKGROUND_IMAGE_ENABLED_DEFAULT_VALUE
        )

    fun getRendererFrameLimit(): Int = preferenceManager.getInt(PREF_KEY_RENDERER_FRAME_LIMIT, 30)

    companion object {
        @Volatile
        private var instance: PreferenceRepository? = null

        private const val SNOWFALL_IS_ENABLED_DEFAULT_VALUE = true
        private const val SNOWFALL_VELOCITY_FACTOR_DEFAULT_VALUE = 3
        private const val SNOWFALL_LIMIT_DEFAULT_VALUE = 80
        private const val SNOWFALL_IS_UNIQUE_RADIUS_ENABLED = true
        private const val SNOWFALL_MIN_RADIUS_DEFAULT_VALUE = 8
        private const val SNOWFALL_MAX_RADIUS_DEFAULT_VALUE = 30
        private const val IS_BACKGROUND_IMAGE_ENABLED_DEFAULT_VALUE = false

        const val PREF_KEY_IS_SNOWFALL_ENABLED =
            "PREF_KEY_IS_SNOWFALL_ENABLED"

        const val PREF_KEY_SNOWFALL_LIMIT =
            "PREF_KEY_SNOWFALL_LIMIT"

        const val PREF_KEY_SNOWFALL_VELOCITY_FACTOR =
            "PREF_KEY_SNOWFALL_VELOCITY_FACTOR"

        const val PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED =
            "PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED"

        const val PREF_KEY_SNOWFALL_MIN_RADIUS =
            "PREF_KEY_SNOWFALL_MIN_RADIUS"

        const val PREF_KEY_SNOWFALL_MAX_RADIUS =
            "PREF_KEY_SNOWFALL_MAX_RADIUS"

        const val PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED =
            "PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED"

        const val PREF_KEY_RENDERER_FRAME_LIMIT =
            "PREF_KEY_RENDERER_FRAME_LIMIT"

        fun getInstance(context: Context): PreferenceRepository =
            instance ?: synchronized(this) {
                instance ?: PreferenceRepository(context).also { instance = it }
            }
    }
}
