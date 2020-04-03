package dev.jatzuk.snowwallpaper.ui.preferences

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.ui.preferences.custom.IntentPreference
import dev.jatzuk.snowwallpaper.utilities.ImageProvider
import dev.jatzuk.snowwallpaper.utilities.ImageProvider.texturesViewModel
import dev.jatzuk.snowwallpaper.viewmodels.TexturesViewModel

class PreferencesFragment : AbstractPreferenceFragment(R.xml.preferences_main) {

    private lateinit var preferenceRepository: PreferenceRepository
    private lateinit var intentPreferences: Array<IntentPreference>
    private lateinit var categoryDisabledDrawable: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceRepository = PreferenceRepository.getInstance(context!!)
        texturesViewModel = ViewModelProvider(this).get(TexturesViewModel::class.java)
        categoryDisabledDrawable =
            ContextCompat.getDrawable(context!!, R.drawable.category_disabled)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = getString(R.string.preferences)
    }

    override fun setUp() {
        intentPreferences = arrayOf(
            findPreference(getString(R.string.pref_key_snowfall_category))!!,
            findPreference(getString(R.string.pref_key_snowflake_category))!!,
            findPreference(getString(R.string.pref_key_background_image_category))!!
        )
    }

    override fun attachObserver() {
        texturesViewModel?.getTextures()?.observe(
            viewLifecycleOwner,
            Observer {
                intentPreferences.forEachIndexed { index, preference ->
                    preference.apply {
                        previewImage =
                            it[ImageProvider.ImageType.values()[index]]?.toDrawable(resources)
                    }
                }
            }
        )

//        todo replace with normal realization
        preferenceRepository.snowfallPreference.observe(
            viewLifecycleOwner,
            Observer { isEnabled ->
                if (!isEnabled) intentPreferences[0].previewImage = categoryDisabledDrawable
            }
        )

        preferenceRepository.snowflakePreference.observe(
            viewLifecycleOwner,
            Observer { isEnabled ->
                if (!isEnabled) intentPreferences[1].previewImage = categoryDisabledDrawable
            }
        )

        preferenceRepository.backgroundImagePreference.observe(
            viewLifecycleOwner,
            Observer { isEnabled ->
                if (!isEnabled) intentPreferences[2].previewImage = categoryDisabledDrawable
            }
        )
    }

    override fun provideBackgroundColor(): Int = Color.YELLOW

    override fun provideBackground(): Drawable? = null
//        ContextCompat.getDrawable(context!!, R.drawable.background_preferences_main_screen)

    companion object {
        const val TAG = "PreferencesFragment"

        fun findOrCreateRetainFragment(fm: FragmentManager): PreferencesFragment {
            return (fm.findFragmentByTag(TAG) as? PreferencesFragment) ?: run {
                PreferencesFragment().also {
                    fm.beginTransaction().add(it, TAG).commit()
                }
            }
        }
    }
}
