package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository

class CustomSwitchPreference(
    context: Context,
    attributeSet: AttributeSet?
) : AbstractPreference(context, attributeSet) {

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
                    PreferenceRepository.PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED -> {
                        preferenceRepository.getIsBackgroundImageEnabled()
                    }
                    else -> true
                }
                setOnClickListener {
                    notifyDependencyChange(shouldDisableDependents())
                    sharedPreferences.edit().putBoolean(key, isChecked).apply()
                    notifyDependencyChange(shouldDisableDependents())
                }
            }
        }
    }

    override fun provideLayout(): Int = R.layout.layout_preference_switcher

    companion object {
        private const val TAG = "CustomSwitchPreference"
    }
}