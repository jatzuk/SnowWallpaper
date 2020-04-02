package dev.jatzuk.snowwallpaper.data.preferences

import android.content.Context
import androidx.preference.PreferenceManager

class PreferenceRepository private constructor(context: Context) {

    private val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)


    val snowfallPreference = PreferenceLiveData(
        context,
        PREF_KEY_IS_SNOWFALL_ENABLED,
        SNOWFALL_IS_ENABLED_DEFAULT_VALUE
    )

    val snowflakePreference = PreferenceLiveData(
        context,
        PREF_KEY_IS_SNOWFLAKE_ENABLED,
        SNOWFLAKE_IS_ENABLED_DEFAULT_VALUE
    )

    val backgroundImagePreference = PreferenceLiveData(
        context,
        PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED,
        BACKGROUND_IMAGE_IS_ENABLED_DEFAULT_VALUE
    )

    fun getIsSnowfallEnabled(): Boolean =
        preferenceManager.getBoolean(
            PREF_KEY_IS_SNOWFALL_ENABLED,
            SNOWFALL_IS_ENABLED_DEFAULT_VALUE
        )

    fun getSnowfallLimit(): Int =
        preferenceManager.getInt(
            PREF_KEY_SNOWFALL_LIMIT,
            SNOWFALL_LIMIT_DEFAULT_VALUE
        )

    fun getSnowfallVelocityFactor(): Int =
        preferenceManager.getInt(
            PREF_KEY_SNOWFALL_VELOCITY_FACTOR,
            SNOWFALL_VELOCITY_FACTOR_DEFAULT_VALUE
        )

    fun getIsSnowfallUniqueRadiusEnabled(): Boolean =
        preferenceManager.getBoolean(
            PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED,
            SNOWFALL_IS_UNIQUE_RADIUS_ENABLED_DEFAULT_VALUE
        )

    fun getSnowfallMinRadius(): Int =
        preferenceManager.getInt(
            PREF_KEY_SNOWFALL_MIN_RADIUS,
            SNOWFALL_MIN_RADIUS_DEFAULT_VALUE
        )

    fun getSnowfallMaxRadius(): Int =
        preferenceManager.getInt(
            PREF_KEY_SNOWFALL_MAX_RADIUS,
            SNOWFALL_MAX_RADIUS_DEFAULT_VALUE
        )

    fun getSnowfallRadiusWhenUniqueDisabled(): Int =
        preferenceManager.getInt(
            PREF_KEY_SNOWFALL_DEFAULT_RADIUS_UNIQUE_RADIUS_DISABLED,
            SNOWFALL_RADIUS_UNIQUE_DISABLED_DEFAULT_VALUE
        )

    fun getIsSnowflakeEnabled(): Boolean =
        preferenceManager.getBoolean(
            PREF_KEY_IS_SNOWFLAKE_ENABLED,
            SNOWFLAKE_IS_ENABLED_DEFAULT_VALUE
        )

    fun getSnowflakeLimit(): Int =
        preferenceManager.getInt(
            PREF_KEY_SNOWFLAKE_LIMIT,
            SNOWFLAKE_LIMIT_DEFAULT_VALUE
        )

    fun getSnowflakeVelocityFactor(): Int =
        preferenceManager.getInt(
            PREF_KEY_SNOWFLAKE_VELOCITY_FACTOR,
            SNOWFLAKE_VELOCITY_DEFAULT_VALUE
        )

    fun getSnowflakeRotationVelocity(): Int =
        preferenceManager.getInt(
            PREF_KEY_SNOWFLAKE_ROTATION_VELOCITY,
            SNOWFLAKE_ROTATION_VELOCITY_DEFAULT_VALUE
        )

    fun getIsSnowflakeUniqueRadiusEnabled(): Boolean =
        preferenceManager.getBoolean(
            PREF_KEY_IS_SNOWFLAKE_UNIQUE_RADIUS_ENABLED,
            SNOWFLAKE_IS_UNIQUE_RADIUS_ENABLED_DEFAULT_VALUE
        )

    fun getSnowflakeMinRadius(): Int =
        preferenceManager.getInt(
            PREF_KEY_SNOWFLAKE_MIN_RADIUS,
            SNOWFLAKE_MIN_RADIUS_DEFAULT_VALUE
        )

    fun getSnowflakeMaxRadius(): Int =
        preferenceManager.getInt(
            PREF_KEY_SNOWFLAKE_MAX_RADIUS,
            SNOWFLAKE_MAX_RADIUS_DEFAULT_VALUE
        )

    fun getSnowflakeRadiusWhenUniqueDisabled(): Int =
        preferenceManager.getInt(
            PREF_KEY_SNOWFLAKE_DEFAULT_RADIUS_UNIQUE_RADIUS_DISABLED,
            SNOWFLAKE_RADIUS_UNIQUE_DISABLED_DEFAULT_VALUE
        )

    fun getIsBackgroundImageEnabled(): Boolean =
        preferenceManager.getBoolean(
            PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED,
            BACKGROUND_IMAGE_IS_ENABLED_DEFAULT_VALUE
        )

    fun getCosineDeviation(): Int =
        preferenceManager.getInt(
            PREF_KEY_COSINE_DEVIATION,
            COSINE_DEVIATION_DEFAULT_VALUE
        )

    fun getRendererFrameLimit(): Int =
        if (getRendererToggleState()) RENDERER_FRAMERATE_MAX_VALUE
        else RENDERER_FRAMERATE_DEFAULT_VALUE

    fun getRendererToggleState(): Boolean =
        preferenceManager.getBoolean(
            PREF_KEY_RENDERER_FRAME_LIMIT,
            RENDERER_FRAMERATE_STATE_DEFAULT_VALUE
        )

    fun setSnowfallTextureSavedPosition(position: Int) {
        preferenceManager.edit().putInt(SNOWFALL_TEXTURE_SAVED_POSITION, position).apply()
    }

    fun getSnowfallTextureSavedPosition(): Int =
        preferenceManager.getInt(
            SNOWFALL_TEXTURE_SAVED_POSITION,
            0
        )

    fun setSnowflakeTextureSavedPosition(position: Int) {
        preferenceManager.edit().putInt(SNOWFLAKE_TEXTURE_SAVED_POSITION, position).apply()
    }

    fun getSnowflakeTextureSavedPosition(): Int =
        preferenceManager.getInt(
            SNOWFLAKE_TEXTURE_SAVED_POSITION,
            0
        )


    fun setBackgroundImageSavedPosition(position: Int) {
        preferenceManager.edit().putInt(BACKGROUND_IMAGE_SAVED_POSITION, position).apply()
    }

    fun getBackgroundImageSavedPosition(): Int =
        preferenceManager.getInt(
            BACKGROUND_IMAGE_SAVED_POSITION,
            0
        )

    companion object {
        @Volatile
        private var instance: PreferenceRepository? = null

        private const val SNOWFALL_IS_ENABLED_DEFAULT_VALUE = true
        private const val SNOWFALL_LIMIT_DEFAULT_VALUE = 80
        private const val SNOWFALL_VELOCITY_FACTOR_DEFAULT_VALUE = 3
        private const val SNOWFALL_IS_UNIQUE_RADIUS_ENABLED_DEFAULT_VALUE = true
        private const val SNOWFALL_MIN_RADIUS_DEFAULT_VALUE = 8
        private const val SNOWFALL_MAX_RADIUS_DEFAULT_VALUE = 30
        private const val SNOWFALL_RADIUS_UNIQUE_DISABLED_DEFAULT_VALUE = 30

        private const val SNOWFLAKE_IS_ENABLED_DEFAULT_VALUE = true
        private const val SNOWFLAKE_LIMIT_DEFAULT_VALUE = 2
        private const val SNOWFLAKE_VELOCITY_DEFAULT_VALUE = 2
        private const val SNOWFLAKE_ROTATION_VELOCITY_DEFAULT_VALUE = 2
        private const val SNOWFLAKE_IS_UNIQUE_RADIUS_ENABLED_DEFAULT_VALUE = true
        private const val SNOWFLAKE_MIN_RADIUS_DEFAULT_VALUE = 30
        private const val SNOWFLAKE_MAX_RADIUS_DEFAULT_VALUE = 60
        private const val SNOWFLAKE_RADIUS_UNIQUE_DISABLED_DEFAULT_VALUE = 60

        private const val BACKGROUND_IMAGE_IS_ENABLED_DEFAULT_VALUE = false

        private const val COSINE_DEVIATION_DEFAULT_VALUE = 1

        const val RENDERER_FRAMERATE_DEFAULT_VALUE = 30
        const val RENDERER_FRAMERATE_MAX_VALUE = 60
        private const val RENDERER_FRAMERATE_STATE_DEFAULT_VALUE = false

        private const val SNOWFALL_TEXTURE_SAVED_POSITION =
            "SNOWFALL_TEXTURE_SAVED_POSITION"

        private const val SNOWFLAKE_TEXTURE_SAVED_POSITION =
            "SNOWFLAKE_TEXTURE_SAVED_POSITION"

        private const val BACKGROUND_IMAGE_SAVED_POSITION =
            "BACKGROUND_IMAGE_SAVED_POSITION"

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

        const val PREF_KEY_SNOWFALL_DEFAULT_RADIUS_UNIQUE_RADIUS_DISABLED =
            "PREF_KEY_SNOWFALL_DEFAULT_RADIUS_UNIQUE_RADIUS_DISABLED"

        const val PREF_KEY_IS_SNOWFLAKE_ENABLED =
            "PREF_KEY_IS_SNOWFLAKE_ENABLED"

        const val PREF_KEY_SNOWFLAKE_LIMIT =
            "PREF_KEY_SNOWFLAKE_LIMIT"

        const val PREF_KEY_SNOWFLAKE_VELOCITY_FACTOR =
            "PREF_KEY_SNOWFLAKE_VELOCITY_FACTOR"

        const val PREF_KEY_IS_SNOWFLAKE_UNIQUE_RADIUS_ENABLED =
            "PREF_KEY_IS_SNOWFLAKE_UNIQUE_RADIUS_ENABLED"

        const val PREF_KEY_SNOWFLAKE_MIN_RADIUS =
            "PREF_KEY_SNOWFLAKE_MIN_RADIUS"

        const val PREF_KEY_SNOWFLAKE_MAX_RADIUS =
            "PREF_KEY_SNOWFLAKE_MAX_RADIUS"

        const val PREF_KEY_SNOWFLAKE_DEFAULT_RADIUS_UNIQUE_RADIUS_DISABLED =
            "PREF_KEY_SNOWFLAKE_DEFAULT_RADIUS_UNIQUE_RADIUS_DISABLED"

        const val PREF_KEY_SNOWFLAKE_ROTATION_VELOCITY =
            "PREF_KEY_SNOWFLAKE_ROTATION_VELOCITY"

        const val PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED =
            "PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED"

        const val PREF_KEY_COSINE_DEVIATION =
            "PREF_KEY_COSINE_DEVIATION"

        const val PREF_KEY_RENDERER_FRAME_LIMIT =
            "PREF_KEY_RENDERER_FRAME_LIMIT"

        fun getInstance(context: Context): PreferenceRepository =
            instance ?: synchronized(this) {
                instance ?: PreferenceRepository(context).also { instance = it }
            }
    }
}
