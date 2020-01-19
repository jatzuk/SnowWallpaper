package dev.jatzuk.snowwallpaper.views.preferences

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.views.imagepicker.BackgroundImagesFragment
import java.io.File
import java.io.FileOutputStream

@Suppress("unused")
class BackgroundImagePreferenceFragment :
    AbstractPreferenceFragment(R.xml.preferences_background_image) {

    override val preferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            val globalSwitcher = getString(R.string.background_image_global_switcher_key)

            when (key) {
                globalSwitcher -> {
                    val isEnabled = sharedPreferences.getBoolean(globalSwitcher, true)
                    sharedPreferences.edit().putBoolean(globalSwitcher, isEnabled).apply()
                    switchDependentPreferences(isEnabled, 1)
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = (getString(R.string.background_image))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                SELECT_PREDEFINED_BACKGROUND_IMAGE_REQUEST_CODE -> {
                    val d = data?.getStringExtra("dummyContent")
                    Toast.makeText(context, d, Toast.LENGTH_SHORT).show()
                }
                SELECT_CUSTOM_BACKGROUND_IMAGE_REQUEST_CODE -> {
                    data?.let {
                        val inp = context?.contentResolver?.openInputStream(it.data!!)
                        val bitmap = BitmapFactory.decodeStream(inp)
                        val name = bitmap.config.name
                        FileOutputStream(File(name)).use { fos ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                        }
                    } ?: return
                }
            }
        }
    }

    companion object {
        private const val TAG = "BackgroundImagePreferenceFragment"
        private const val SELECT_PREDEFINED_BACKGROUND_IMAGE_REQUEST_CODE = 1
        private const val SELECT_CUSTOM_BACKGROUND_IMAGE_REQUEST_CODE = 2
    }
}
