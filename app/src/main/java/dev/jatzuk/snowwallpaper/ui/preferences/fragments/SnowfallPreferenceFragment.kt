package dev.jatzuk.snowwallpaper.ui.preferences.fragments

import android.os.Bundle
import androidx.lifecycle.Observer
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceLiveData
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository.Companion.PREF_KEY_IS_SNOWFALL_ENABLED
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository.Companion.SNOWFALL_IS_ENABLED_DEFAULT_VALUE
import dev.jatzuk.snowwallpaper.ui.imagepicker.TexturedAbstractDialogFragment
import dev.jatzuk.snowwallpaper.ui.preferences.custom.IntentPreference
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

@Suppress("unused")
class SnowfallPreferenceFragment : AbstractPreferenceFragment(R.xml.preferences_snowfall) {

    override fun setUp() {
        preferenceLiveData = PreferenceLiveData(
            requireContext(),
            PREF_KEY_IS_SNOWFALL_ENABLED,
            SNOWFALL_IS_ENABLED_DEFAULT_VALUE
        )

        findPreference<IntentPreference>(getString(R.string.pref_key_snowfall_select_texture))!!
            .setOnPreferenceClickListener {
                startDialogFragment(SnowfallDialogFragment())
                true
            }
    }

    override fun attachObserver() {
        preferenceLiveData.observe(
            viewLifecycleOwner,
            Observer {
                val textureType = TextureProvider.TextureType.SNOWFALL_TEXTURE
                if (!it) textureCache.remove(textureType)
                else if (textureCache[textureType] == null) {
                    textureCache[textureType] =
                        TextureProvider.assignDefaultTexture(requireContext(), textureType)
                    preferenceRepository.setSnowfallTextureSavedPosition(0)
                }

                texturesViewModel.updateTexture(textureType)
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = getString(R.string.snowfall_setup_fragment_title)
    }

    class SnowfallDialogFragment : TexturedAbstractDialogFragment(
        intArrayOf(
            R.drawable.texture_snowflake_0,
            R.drawable.texture_snowfall_0
        ),
        TextureProvider.TextureType.SNOWFALL_TEXTURE
    ) {

        override fun setTextureSavedPosition(position: Int) {
            preferenceRepository.setSnowfallTextureSavedPosition(position)
        }

        override fun getTextureSavedPosition(): Int =
            preferenceRepository.getSnowfallTextureSavedPosition()
    }
}
