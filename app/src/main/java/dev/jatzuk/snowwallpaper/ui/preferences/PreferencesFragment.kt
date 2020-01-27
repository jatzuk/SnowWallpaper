package dev.jatzuk.snowwallpaper.ui.preferences

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Observer
import androidx.preference.Preference
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

class PreferencesFragment : AbstractPreferenceFragment(R.xml.preferences_main) {

    var isBackgroundImageEnabled = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = getString(R.string.preferences)
    }

    override fun setUp() {} // stub

    override fun attachObserver() {
        PreferenceRepository.getInstance(context!!).backgroundImagePreference.observe(
            viewLifecycleOwner,
            Observer { isBackgroundImageEnabled = it })
    }

    override fun onResume() {
        super.onResume()
        val bitmap = ImageProvider.loadThumbnailImage(context!!)
        val thumbnailIcon = bitmap?.toDrawable(resources) ?: BitmapDrawable(resources, bitmap)
        val backgroundImage =
            findPreference<Preference>(getString(R.string.background_image_global_switcher_key))
        backgroundImage?.icon = thumbnailIcon
    }
}
