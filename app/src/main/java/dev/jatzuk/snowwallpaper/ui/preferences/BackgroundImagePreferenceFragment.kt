package dev.jatzuk.snowwallpaper.ui.preferences

import android.graphics.Color
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.ui.imagepicker.AbstractDialogFragment
import dev.jatzuk.snowwallpaper.ui.preferences.custom.IntentPreference
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

@Suppress("unused")
class BackgroundImagePreferenceFragment :
    AbstractPreferenceFragment(R.xml.preferences_background_image) {

    override fun setUp() {
//        todo
        findPreference<IntentPreference>("background_image_select_image")!!.apply {
            setOnPreferenceClickListener {
                startDialogFragmentTransition()
                true
            }
        }
    }

    override fun attachObserver() {}

    override fun provideDialogFragment(): AbstractDialogFragment? = BackgroundImageDialogFragment()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = (getString(R.string.background_image))
    }

    override fun provideBackgroundColor(): Int = Color.BLUE

//    override fun provideBackground(): Drawable? = ContextCompat.getDrawable(context!!, R.drawable.b1)

    class BackgroundImageDialogFragment : AbstractDialogFragment(
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
