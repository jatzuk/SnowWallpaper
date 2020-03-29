package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.ui.preferences.custom.IntentPreference
import dev.jatzuk.snowwallpaper.ui.preferences.texturepicker.AbstractDialogFragment
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

@Suppress("unused")
class SnowflakePreferenceFragment : AbstractPreferenceFragment(R.xml.preferences_snowflake) {

    private lateinit var texturePickerPreference: IntentPreference

    override fun setUp() {
        texturePickerPreference =
            findPreference("snowflake_select_texture")!! // todo
        texturePickerPreference.apply {
            setOnPreferenceClickListener {
                val pickerDialogFragment = SnowflakeDialogFragment()
                pickerDialogFragment.setTargetFragment(
                    childFragmentManager.findFragmentById(this@SnowflakePreferenceFragment.id),
                    AbstractDialogFragment.SELECT_CUSTOM_IMAGE
                )
                pickerDialogFragment.show(childFragmentManager.beginTransaction(), "") // todo
                true
            }
        }

    }

    override fun attachObserver() {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = "Foreground snowflakes"
    }

    override fun provideBackground(): Drawable? = null

    override fun provideBackgroundColor(): Int = Color.CYAN

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        childFragmentManager.fragments[0].onActivityResult(requestCode, resultCode, data)
    }

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
