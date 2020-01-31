package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.SwitchPreferenceCompat
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository

@Suppress("unused")
class SnowfallPreferenceFragment : AbstractPreferenceFragment(R.xml.preferences_snowfall) {

    private val preferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            val isEnabled = PreferenceRepository.PREF_KEY_IS_SNOWFALL_ENABLED
            val isRandomRadius = PreferenceRepository.PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED

            when (key) {
                isEnabled -> {
                    val state = sharedPreferences.getBoolean(isEnabled, true)
                    switchDependentPreferences(state, 1)
                }
                isRandomRadius -> {
                    val state = sharedPreferences.getBoolean(isRandomRadius, true)
                    switchDependentPreferences(state, 4)
                }
            }
        }

    override fun setUp() {
        val isCategoryEnabled = findPreference<SwitchPreferenceCompat>(
            PreferenceRepository.PREF_KEY_IS_SNOWFALL_ENABLED
        )!!.isChecked
        switchDependentPreferences(isCategoryEnabled, 1)

        if (isCategoryEnabled) {
            val isUniqueRadiusChecked = findPreference<SwitchPreferenceCompat>(
                PreferenceRepository.PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED
            )!!.isChecked
            switchDependentPreferences(isUniqueRadiusChecked, 4)
        }
    }

    override fun attachObserver() {
        preferenceManager.sharedPreferences
            .registerOnSharedPreferenceChangeListener(preferencesListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value =
            getString(R.string.background_snowflakes) // todo(change attrib name)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(preferencesListener)
    }
}
