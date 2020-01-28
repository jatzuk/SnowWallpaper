package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.SwitchPreferenceCompat
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.utilities.Logger.logging

@Suppress("unused")
class SnowfallPreferenceFragment : AbstractPreferenceFragment(R.xml.preferences_snowfall) {

    private val preferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            val isEnabled = PreferenceRepository.PREF_KEY_IS_SNOWFALL_ENABLED
            val limit = PreferenceRepository.PREF_KEY_SNOWFALL_LIMIT
            val velocityFactor = PreferenceRepository.PREF_KEY_SNOWFALL_VELOCITY_FACTOR
            val isRandomRadius = PreferenceRepository.PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED
            val minRadius = PreferenceRepository.PREF_KEY_SNOWFALL_MIN_RADIUS
            val maxRadius = PreferenceRepository.PREF_KEY_BACKGROUND_SNOWFALL_MAX_RADIUS

            when (key) {
                isEnabled -> {
                    val state = sharedPreferences.getBoolean(isEnabled, true)
                    switchDependentPreferences(state, 1)
                }
                limit -> {
                    val currentLimit = sharedPreferences.getInt(limit, 10)
                    sharedPreferences.edit().putInt(limit, currentLimit).apply()
                }
                velocityFactor -> {
                    val velocityFactoryValue = sharedPreferences.getInt(velocityFactor, 2)
                    sharedPreferences.edit().putInt(velocityFactor, velocityFactoryValue).apply()
                }
                isRandomRadius -> {
                    val state = sharedPreferences.getBoolean(isRandomRadius, true)
                    switchDependentPreferences(state, 4)
                }
                minRadius -> {
                    val minRadiusValue = sharedPreferences.getInt(minRadius, 10)
                    sharedPreferences.edit().putInt(minRadius, minRadiusValue).apply()
                }
                maxRadius -> {
                    val maxRadiusValue = sharedPreferences.getInt(maxRadius, 30)
                    sharedPreferences.edit().putInt(maxRadius, maxRadiusValue).apply()
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
