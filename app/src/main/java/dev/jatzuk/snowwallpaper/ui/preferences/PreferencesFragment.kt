package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.core.graphics.drawable.toDrawable
import androidx.preference.Preference
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

class PreferencesFragment : AbstractPreferenceFragment(R.xml.preferences_main) {
    override val preferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> } // done with xml

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = getString(R.string.preferences)
    }

    override fun setUp() {} // stub

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(
            preferencesListener
        )
        val bitmap = ImageProvider.loadThumbnailImage(context!!)
        val thumbnailIcon = bitmap?.toDrawable(resources) ?: BitmapDrawable(resources, bitmap)
        val backgroundImage =
            findPreference<Preference>(getString(R.string.background_image_global_switcher_key))
        backgroundImage?.icon = thumbnailIcon

    }
}
