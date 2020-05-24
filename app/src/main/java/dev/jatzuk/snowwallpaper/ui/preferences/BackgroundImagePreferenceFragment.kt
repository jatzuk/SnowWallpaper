package dev.jatzuk.snowwallpaper.ui.preferences

import android.graphics.Color
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.ui.imagepicker.TexturedAbstractDialogFragment
import dev.jatzuk.snowwallpaper.ui.preferences.custom.IntentPreference
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

@Suppress("unused")
class BackgroundImagePreferenceFragment :
    AbstractPreferenceFragment(R.xml.preferences_background_image) {

    override fun setUp() {
        findPreference<IntentPreference>(getString(R.string.pref_key_background_image_select_texture))!!.apply {
            setOnPreferenceClickListener {
                startDialogFragment(BackgroundImageDialogFragment())
                true
            }
        }
    }

    override fun attachObserver() {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value =
            (getString(R.string.background_image_fragment_title))
    }

    override fun provideBackgroundColor(): Int = Color.BLUE

//    override fun provideBackground(): Drawable? = ContextCompat.getDrawable(context!!, R.drawable.b1)

    class BackgroundImageDialogFragment : TexturedAbstractDialogFragment(
        intArrayOf(
            R.drawable.background_image,
            R.drawable.b0,
            R.drawable.b1,
            R.drawable.b2
        ),
        ImageProvider.ImageType.BACKGROUND_IMAGE
    ) {

        override fun provideTexturePositionSavePosition(position: Int) {
            preferenceRepository.setBackgroundImageSavedPosition(position)
        }

        override fun provideTexturePositionLoadPosition(): Int =
            preferenceRepository.getBackgroundImageSavedPosition()
    }

    companion object {
        private const val TAG = "BackgroundImagePreferenceFragment"
    }
}
