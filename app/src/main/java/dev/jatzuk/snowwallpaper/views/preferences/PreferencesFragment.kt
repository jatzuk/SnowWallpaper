package dev.jatzuk.snowwallpaper.views.preferences

import android.content.SharedPreferences
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R

class PreferencesFragment : AbstractPreferenceFragment(R.xml.preferences_main) {
    override val preferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> } // done with xml

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = getString(R.string.preferences)
    }

    override fun setUp() {} // stub
}
