package dev.jatzuk.snowwallpaper.ui.preferences

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.lifecycle.Observer
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository

class PreferencesFragment : AbstractPreferenceFragment(R.xml.preferences_main) {

    var isBackgroundImageEnabled = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = getString(R.string.preferences)
    }

    override fun setUp() {
//        todo load image preview
//        val bitmap =
//            ImageProvider.loadThumbnailImage(context!!) // todo (check if it's first start of app, so background image is null, or set default images)
//        val thumbnailIcon = bitmap?.toDrawable(resources) ?: BitmapDrawable(resources, bitmap)
//        val backgroundImage =
//            findPreference<Preference>(getString(R.string.background_image_global_switcher_key))
//        backgroundImage?.icon = thumbnailIcon

    } // stub

    override fun attachObserver() {
        PreferenceRepository.getInstance(context!!).backgroundImagePreference.observe(
            viewLifecycleOwner,
            Observer { isBackgroundImageEnabled = it }
        )
    }

    override fun provideBackgroundColor(): Int = Color.YELLOW

    override fun provideBackground(): Drawable? = null
//        ContextCompat.getDrawable(context!!, R.drawable.background_preferences_main_screen)

}
