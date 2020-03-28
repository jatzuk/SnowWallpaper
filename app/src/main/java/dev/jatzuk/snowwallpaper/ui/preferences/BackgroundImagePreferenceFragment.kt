package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.ui.preferences.custom.IntentPreference
import dev.jatzuk.snowwallpaper.ui.preferences.texturepicker.AbstractDialogFragment
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

@Suppress("unused")
class BackgroundImagePreferenceFragment :
    AbstractPreferenceFragment(R.xml.preferences_background_image) {

    private lateinit var texturePickerPreference: IntentPreference

    override fun setUp() {
        texturePickerPreference = findPreference("background_image_select_image")!!
        texturePickerPreference.apply {
            setOnPreferenceClickListener {
                val pickerDialogFragment = BackgroundImageDialogFragment()
                pickerDialogFragment.setTargetFragment(
                    childFragmentManager.findFragmentById(this@BackgroundImagePreferenceFragment.id),
                    AbstractDialogFragment.SELECT_CUSTOM_IMAGE
                )
//                pickerDialogFragment.show(childFragmentManager.beginTransaction(), "") // todo
                childFragmentManager.beginTransaction()
                    .add(pickerDialogFragment, "")
                    .addToBackStack(null)
                    .commit()
                true
            }
        }
    }

    override fun attachObserver() {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = (getString(R.string.background_image))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        childFragmentManager.fragments[0].onActivityResult(requestCode, resultCode, data)
    }

    override fun provideBackgroundColor(): Int = Color.BLUE

//    override fun provideBackground(): Drawable? = ContextCompat.getDrawable(context!!, R.drawable.b1)

    class BackgroundImageDialogFragment : AbstractDialogFragment(
        intArrayOf(
            R.drawable.background_image
//            ,
//            R.drawable.b0,
//            R.drawable.b1,
//            R.drawable.b2
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
        private const val SELECT_CUSTOM_BACKGROUND_IMAGE_REQUEST_CODE = 2
    }
}
