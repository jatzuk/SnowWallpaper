package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.ui.preferences.custom.IntentPreference
import dev.jatzuk.snowwallpaper.ui.preferences.texturepicker.PickerDialogFragment
import dev.jatzuk.snowwallpaper.ui.preferences.texturepicker.PickerDialogFragment.Companion.SELECT_CUSTOM_SNOWFALL_TEXTURE

@Suppress("unused")
class SnowfallPreferenceFragment : AbstractPreferenceFragment(R.xml.preferences_snowfall) {

    private lateinit var texturePickerPreference: IntentPreference

    override fun setUp() {
        texturePickerPreference = findPreference("snowfall_select_texture")!! // todo
        texturePickerPreference.apply {
            setOnPreferenceClickListener {
                val pickerDialogFragment = PickerDialogFragment()
                pickerDialogFragment.setTargetFragment(
                    childFragmentManager.findFragmentById(this@SnowfallPreferenceFragment.id),
                    SELECT_CUSTOM_SNOWFALL_TEXTURE
                )
                pickerDialogFragment.show(childFragmentManager.beginTransaction(), "") // todo
                true
            }
        }
    }

    override fun attachObserver() {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value =
            getString(R.string.background_snowflakes) // todo(change attrib name)
    }

    override fun provideBackground(): Drawable? = null
//        BitmapFactory.decodeResource(resources, R.drawable.b2).toDrawable(resources)

    override fun provideBackgroundColor(): Int = Color.CYAN

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        childFragmentManager.fragments[0].onActivityResult(requestCode, resultCode, data)
    }
}
