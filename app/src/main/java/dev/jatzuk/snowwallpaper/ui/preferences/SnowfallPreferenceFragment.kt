package dev.jatzuk.snowwallpaper.ui.preferences

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.ui.imagepicker.AbstractDialogFragment
import dev.jatzuk.snowwallpaper.ui.preferences.custom.IntentPreference
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

@Suppress("unused")
class SnowfallPreferenceFragment : AbstractPreferenceFragment(R.xml.preferences_snowfall) {

    override fun setUp() {
        findPreference<IntentPreference>(getString(R.string.pref_key_snowfall_select_texture))!!.apply {
            setOnPreferenceClickListener {
                startDialogFragment(SnowfallDialogFragment())
                true
            }
        }
    }

    override fun attachObserver() {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = getString(R.string.snowfall_setup_fragment_title)
    }

    override fun provideBackground(): Drawable? = null
//        BitmapFactory.decodeResource(resources, R.drawable.b2).toDrawable(resources)

    override fun provideBackgroundColor(): Int = Color.CYAN

    class SnowfallDialogFragment : AbstractDialogFragment(
        intArrayOf(R.drawable.texture_snowflake, R.drawable.texture_snowfall),
        ImageProvider.ImageType.SNOWFALL_TEXTURE
    ) {

        override fun provideTexturePositionSavePosition(position: Int) {
            preferenceRepository.setSnowfallTextureSavedPosition(position)
        }

        override fun provideTexturePositionLoadPosition(): Int =
            preferenceRepository.getSnowfallTextureSavedPosition()
    }
}
