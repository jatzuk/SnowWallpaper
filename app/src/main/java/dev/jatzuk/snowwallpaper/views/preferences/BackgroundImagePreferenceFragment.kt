package dev.jatzuk.snowwallpaper.views.preferences

import android.content.SharedPreferences
import androidx.preference.SwitchPreferenceCompat
import dev.jatzuk.snowwallpaper.R

@Suppress("unused")
class BackgroundImagePreferenceFragment : AbstractPreferenceFragment(R.xml.preferences_background_image) {
    override val preferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            val globalSwitcher = getString(R.string.background_image_global_switcher_key)

            when (key) {
                globalSwitcher -> {
                    val isEnabled = sharedPreferences.getBoolean(globalSwitcher, true)
                    sharedPreferences.edit().putBoolean(globalSwitcher, isEnabled).apply()
                    switchDependentPreferences(isEnabled, 1)
                }
            }
        }

    override fun setUp() {
        val isEnabled = findPreference<SwitchPreferenceCompat>(
            getString(R.string.background_image_global_switcher_key)
        )!!.isChecked
        switchDependentPreferences(isEnabled, 1)
    }

    companion object {
        private const val TAG = "BackgroundImagePreferenceFragment"
    }
}
