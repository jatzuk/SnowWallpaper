package dev.jatzuk.snowwallpaper.ui.preferences.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository.Companion.PREF_KEY_RENDERER_FRAME_LIMIT
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository.Companion.RENDERER_FRAMERATE_DEFAULT_VALUE
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository.Companion.RENDERER_FRAMERATE_MAX_VALUE

class CustomSwitchPreference(
    context: Context,
    attributeSet: AttributeSet?
) : AbstractPreference(context, attributeSet) {

    init {
        if (backgroundImage == null) {
            backgroundImage = ContextCompat.getDrawable(context, R.drawable.b0)
        }
    }

    override fun setupPreference(view: View) {
        view.run {
            findViewById<SwitchCompat>(R.id.switchWidget).run {
                isChecked = when (key) {
                    PreferenceRepository.PREF_KEY_IS_SNOWFALL_ENABLED -> {
                        preferenceRepository.getIsSnowfallEnabled()
                    }
                    PreferenceRepository.PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED -> {
                        preferenceRepository.getIsSnowfallUniqueRadiusEnabled()
                    }

                    PreferenceRepository.PREF_KEY_IS_SNOWFLAKE_ENABLED -> {
                        preferenceRepository.getIsSnowflakeEnabled()
                    }
                    PreferenceRepository.PREF_KEY_IS_SNOWFLAKE_UNIQUE_RADIUS_ENABLED -> {
                        preferenceRepository.getIsSnowflakeUniqueRadiusEnabled()
                    }

                    PreferenceRepository.PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED -> {
                        preferenceRepository.getIsBackgroundImageEnabled()
                    }

                    PreferenceRepository.PREF_KEY_IS_ROLL_SENSOR_ENABLED -> {
                        preferenceRepository.getIsRollSensorEnabled()
                    }
                    PreferenceRepository.PREF_KEY_IS_PITCH_SENSOR_ENABLED -> {
                        preferenceRepository.getIsPitchSensorEnabled()
                    }

                    // for the same code style
                    @Suppress("RemoveRedundantQualifierName")
                    PreferenceRepository.PREF_KEY_RENDERER_FRAME_LIMIT -> {
                        preferenceRepository.getRendererToggleState()
                    }
                    else -> true
                }
                setOnClickListener {
                    notifyDependencyChange(shouldDisableDependents())
                    updateSummary(isChecked)
                    sharedPreferences.edit().putBoolean(key, isChecked).apply()
                    notifyDependencyChange(shouldDisableDependents())

                    // separate update call because there are no dependencies
                    if (key == PREF_KEY_RENDERER_FRAME_LIMIT) notifyChanged()
                }

                updateSummary(isChecked)
            }
        }
    }

    override fun provideLayout(): Int = R.layout.preference_switcher

    private fun updateSummary(isChecked: Boolean) {
        val summary = when (key) {
            PREF_KEY_RENDERER_FRAME_LIMIT -> {
                if (!isChecked) RENDERER_FRAMERATE_DEFAULT_VALUE.toString()
                else RENDERER_FRAMERATE_MAX_VALUE.toString()
            }
            else -> {
                if (isChecked) summaryStringDefault
                else context.getString(R.string.switcher_disabled_label)
            }
        }
        summaryString = summary
    }

    companion object {
        private const val TAG = "CustomSwitchPreference"
    }
}
