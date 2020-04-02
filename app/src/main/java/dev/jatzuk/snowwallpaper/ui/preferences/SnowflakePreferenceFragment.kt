package dev.jatzuk.snowwallpaper.ui.preferences

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.ui.imagepicker.AbstractDialogFragment
import dev.jatzuk.snowwallpaper.ui.preferences.custom.IntentPreference
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

@Suppress("unused")
class SnowflakePreferenceFragment : AbstractPreferenceFragment(R.xml.preferences_snowflake) {

    override fun setUp() {
        findPreference<IntentPreference>(getString(R.string.pref_key_snowflake_select_texture))!!.apply {
            setOnPreferenceClickListener {
                startDialogFragmentTransition()
                true
            }
        }
    }

    override fun attachObserver() {}

    override fun provideDialogFragment(): AbstractDialogFragment? = SnowflakeDialogFragment()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = getString(R.string.snowflake_setup_fragment_title)
    }

    override fun provideBackground(): Drawable? = null

    override fun provideBackgroundColor(): Int = Color.CYAN

    class SnowflakeDialogFragment : AbstractDialogFragment(
        intArrayOf(
            R.drawable.texture_snowflake,
            R.drawable.texture_snowfall,
            R.drawable.category_disabled,
            R.drawable.ic_disabled_64dp
        ),
        ImageProvider.ImageType.SNOWFLAKE_TEXTURE
    ) {

        override fun provideTexturePositionSavePosition(position: Int) {
            preferenceRepository.setSnowflakeTextureSavedPosition(position)
        }

        override fun provideTexturePositionLoadPosition(): Int =
            preferenceRepository.getSnowflakeTextureSavedPosition()
    }
}
