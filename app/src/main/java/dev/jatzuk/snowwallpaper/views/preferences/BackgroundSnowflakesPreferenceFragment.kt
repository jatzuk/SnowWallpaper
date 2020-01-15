package dev.jatzuk.snowwallpaper.views.preferences

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.SwitchPreferenceCompat
import dev.jatzuk.snowwallpaper.R

@Suppress("unused")
class BackgroundSnowflakesPreferenceFragment :
    AbstractPreferenceFragment(R.xml.preferences_background_snowflakes) {

    override val preferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            val globalSwitcher = getString(R.string.background_snowflakes_global_switcher_key)
            val snowflakesLimit = getString(R.string.background_snowflakes_limit_key)
            val velocityFactor = getString(R.string.background_snowflakes_velocity_factor_key)
            val randomRadius = getString(R.string.background_snowflakes_random_radius_key)
            val minRadius = getString(R.string.background_snowflakes_min_radius_key)
            val maxRadius = getString(R.string.background_snowflakes_max_radius_key)

            when (key) {
                globalSwitcher -> {
                    val state = sharedPreferences.getBoolean(globalSwitcher, true)
                    switchDependentPreferences(state, 1)
                }
                snowflakesLimit -> {
                    val limit = sharedPreferences.getInt(snowflakesLimit, 10)
                    sharedPreferences.edit().putInt(snowflakesLimit, limit).apply()
                }
                velocityFactor -> {
                    val velocityFactoryValue = sharedPreferences.getInt(velocityFactor, 2)
                    sharedPreferences.edit().putInt(velocityFactor, velocityFactoryValue).apply()
                }
                randomRadius -> {
                    val state = sharedPreferences.getBoolean(randomRadius, true)
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
        val isUniqueRadiusChecked = findPreference<SwitchPreferenceCompat>(
            getString(R.string.background_snowflakes_random_radius_key)
        )!!.isChecked
        switchDependentPreferences(isUniqueRadiusChecked, 4)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = getString(R.string.background_snowflakes) // todo(change attrib name)
    }
}
