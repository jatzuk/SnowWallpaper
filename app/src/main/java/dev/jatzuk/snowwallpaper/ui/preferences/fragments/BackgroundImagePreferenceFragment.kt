package dev.jatzuk.snowwallpaper.ui.preferences.fragments

import android.os.Bundle
import androidx.lifecycle.Observer
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceLiveData
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository.Companion.BACKGROUND_IMAGE_IS_ENABLED_DEFAULT_VALUE
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository.Companion.PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED
import dev.jatzuk.snowwallpaper.ui.imagepicker.TexturedAbstractDialogFragment
import dev.jatzuk.snowwallpaper.ui.preferences.custom.IntentPreference
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

@Suppress("unused")
class BackgroundImagePreferenceFragment :
    AbstractPreferenceFragment(R.xml.preferences_background_image) {

    override fun setUp() {
        preferenceLiveData = PreferenceLiveData(
            requireContext(),
            PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED,
            BACKGROUND_IMAGE_IS_ENABLED_DEFAULT_VALUE
        )

        findPreference<IntentPreference>(getString(R.string.pref_key_background_image_select_texture))!!
            .setOnPreferenceClickListener {
                startDialogFragment(BackgroundImageDialogFragment())
                true
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = (getString(R.string.background_image_fragment_title))
    }

    override fun attachObserver() {
        preferenceLiveData.observe(
            viewLifecycleOwner,
            Observer {
                val textureType = TextureProvider.TextureType.BACKGROUND_IMAGE
                if (!it) textureCache.remove(textureType)
                else if (textureCache[textureType] == null) {
                    textureCache[textureType] =
                        TextureProvider.assignDefaultTexture(requireContext(), textureType)
                    preferenceRepository.setBackgroundImageSavedPosition(0)
                }

                texturesViewModel.updateTexture(textureType)
            }
        )
    }

    class BackgroundImageDialogFragment : TexturedAbstractDialogFragment(
        intArrayOf(
            R.drawable.background_image_0,
            R.drawable.background_image_1,
            R.drawable.background_image_2,
            R.drawable.background_image_3,
            R.drawable.background_image_4,
            R.drawable.background_image_5,
            R.drawable.background_image_6,
            R.drawable.background_image_7,
            R.drawable.background_image_8,
        ),
        TextureProvider.TextureType.BACKGROUND_IMAGE
    ) {

        override fun setTextureSavedPosition(position: Int) {
            preferenceRepository.setBackgroundImageSavedPosition(position)
        }

        override fun getTextureSavedPosition(): Int =
            preferenceRepository.getBackgroundImageSavedPosition()
    }

    companion object {
        private const val TAG = "BackgroundImagePreferenceFragment"
    }
}
