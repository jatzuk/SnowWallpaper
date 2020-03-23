package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.ui.imagepicker.BackgroundImagesFragment
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

@Suppress("unused")
class BackgroundImagePreferenceFragment :
    AbstractPreferenceFragment(R.xml.preferences_background_image) {

    override fun setUp() {
//        val isEnabled = findPreference<SwitchPreferenceCompat>(
//            getString(R.string.background_image_global_switcher_key)
//        )!!.isChecked
//        switchDependentPreferences(isEnabled, 1)

        val predefinedImagePicker = findPreference<Preference>(
            getString(R.string.background_image_pick_predefined_image_key)
        )
        predefinedImagePicker?.setOnPreferenceClickListener {
            val columnCount =
                if (resources.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.preferences_container,
                    BackgroundImagesFragment.newInstance(columnCount)
                )
                .addToBackStack(null)
                .commit()
            true
        }

        val customImagePicker = findPreference<Preference>(
            getString(R.string.background_image_pick_custom_image_key)
        )
        customImagePicker?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            val pickIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).apply { type = "image/*" }
            val chooser =
                Intent.createChooser(intent, getString(R.string.action_choose_image)).apply {
                    putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
                }

            intent.resolveActivity(context?.packageManager!!)?.let {
                startActivityForResult(chooser, SELECT_CUSTOM_BACKGROUND_IMAGE_REQUEST_CODE)
            }
            true
        }
    }

    override fun attachObserver() {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = (getString(R.string.background_image))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                SELECT_CUSTOM_BACKGROUND_IMAGE_REQUEST_CODE -> {
                    data?.let {
                        val cr = context?.contentResolver
                        val stringType = cr?.getType(it.data!!)
                        if (stringType?.substringBefore("/") == "image") {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                context?.contentResolver,
                                it.data!!
                            )
                            ImageProvider.saveImage(
                                context!!,
                                ImageProvider.ImageType.BACKGROUND_IMAGE,
                                bitmap
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "ff",
                                Toast.LENGTH_SHORT
                            ).show()
                        }//todo(not image)
                    }
                }
            }
        }
    }

    override fun provideBackgroundColor(): Int = Color.BLUE

//    override fun provideBackground(): Drawable? = ContextCompat.getDrawable(context!!, R.drawable.b1)

    companion object {
        private const val TAG = "BackgroundImagePreferenceFragment"
        private const val SELECT_CUSTOM_BACKGROUND_IMAGE_REQUEST_CODE = 2
    }
}
