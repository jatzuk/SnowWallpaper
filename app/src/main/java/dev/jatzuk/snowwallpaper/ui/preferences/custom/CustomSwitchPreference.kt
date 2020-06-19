package dev.jatzuk.snowwallpaper.ui.preferences.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository

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
                    else -> true
                }
                setOnClickListener {
                    notifyDependencyChange(shouldDisableDependents())
                    updateSummary(isChecked)
                    sharedPreferences.edit().putBoolean(key, isChecked).apply()
                    notifyDependencyChange(shouldDisableDependents())
                }

                updateSummary(isChecked)
            }
        }
    }

    override fun provideLayout(): Int = R.layout.preference_switcher

    private fun updateSummary(isChecked: Boolean) {
        summaryString =
            if (isChecked) summaryStringDefault
            else context.getString(R.string.switcher_disabled_label)
    }
}
