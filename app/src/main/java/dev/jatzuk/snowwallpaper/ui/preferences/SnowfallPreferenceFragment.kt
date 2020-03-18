package dev.jatzuk.snowwallpaper.ui.preferences

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.ui.preferences.custom.IntentPreference
import dev.jatzuk.snowwallpaper.ui.preferences.texturepicker.PickerDialogFragment

@Suppress("unused")
class SnowfallPreferenceFragment : AbstractPreferenceFragment(R.xml.preferences_snowfall) {

    private lateinit var texturePickerPreference: IntentPreference

    override fun setUp() {
        texturePickerPreference = findPreference("snowfall_select_texture")!! // todo
        texturePickerPreference.apply {
            setOnPreferenceClickListener {
                PickerDialogFragment()
                    .show(childFragmentManager.beginTransaction(), "") // todo
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
}
