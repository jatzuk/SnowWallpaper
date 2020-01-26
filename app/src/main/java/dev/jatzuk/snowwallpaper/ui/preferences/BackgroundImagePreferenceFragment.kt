package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.SharedPreferenceRepository
import dev.jatzuk.snowwallpaper.utilities.ImageProvider
import dev.jatzuk.snowwallpaper.ui.imagepicker.BackgroundImagesFragment

@Suppress("unused")
class BackgroundImagePreferenceFragment :
    AbstractPreferenceFragment(R.xml.preferences_background_image) {

    override val preferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            val globalSwitcher = SharedPreferenceRepository.PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED

            when (key) {
                globalSwitcher -> {
//                    val isEnabled = sharedPreferences.getBoolean(globalSwitcher, true)
//                    sharedPreferences.edit().putBoolean(globalSwitcher, isEnabled).apply()
//                    switchDependentPreferences(isEnabled, 1)

                    preferenceRepository.run {
                        getBackgroundImageSharedPreference().observe(viewLifecycleOwner, Observer {
//                            setBackgroundImageEnabled(it)
                            switchDependentPreferences(it, 1)
                        })
                    }
                }
            }
        }

    override fun setUp() {
        val isEnabled = findPreference<SwitchPreferenceCompat>(
            getString(R.string.background_image_global_switcher_key)
        )!!.isChecked
        switchDependentPreferences(isEnabled, 1)

        val predefinedImagePicker = findPreference<Preference>(
            getString(R.string.background_image_pick_predefined_image_key)
        )
        predefinedImagePicker?.setOnPreferenceClickListener {
            val columnCount =
                if (resources.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4
            fragmentManager?.beginTransaction()
                ?.replace(
                    R.id.preferences_container,
                    BackgroundImagesFragment.newInstance(columnCount)
                )
                ?.addToBackStack(null)
                ?.commit()
            true
        }

        val customImagePicker = findPreference<Preference>(
            getString(R.string.background_image_pick_custom_image_key)
        )
        customImagePicker?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            val pickIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
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
                        if (stringType?.substringBefore("/") == "image")
                            ImageProvider.saveBackgroundImage(context!!, it.data!!)
                        else Toast.makeText(
                            context,
                            "ff",
                            Toast.LENGTH_SHORT
                        ).show()//todo(not image)
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "BackgroundImagePreferenceFragment"
        private const val SELECT_CUSTOM_BACKGROUND_IMAGE_REQUEST_CODE = 2
    }
}
