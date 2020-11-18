package dev.jatzuk.snowwallpaper.ui.preferences

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.lifecycle.Observer
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceLiveData
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository.Companion.PREF_KEY_IS_SNOWFLAKE_ENABLED
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository.Companion.SNOWFLAKE_IS_ENABLED_DEFAULT_VALUE
import dev.jatzuk.snowwallpaper.ui.imagepicker.TexturedAbstractDialogFragment
import dev.jatzuk.snowwallpaper.ui.preferences.custom.IntentPreference
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

@Suppress("unused")
class SnowflakePreferenceFragment : AbstractPreferenceFragment(R.xml.preferences_snowflake) {

    override fun setUp() {
        preferenceLiveData = PreferenceLiveData(
            requireContext(),
            PREF_KEY_IS_SNOWFLAKE_ENABLED,
            SNOWFLAKE_IS_ENABLED_DEFAULT_VALUE
        )

        findPreference<IntentPreference>(getString(R.string.pref_key_snowflake_select_texture))!!
            .setOnPreferenceClickListener {
                startDialogFragment(SnowflakeDialogFragment())
                true
            }

        findPreference<IntentPreference>(getString(R.string.pref_key_snowflake_rotation_axes))!!
            .setOnPreferenceClickListener {
                startDialogFragment(
                    SnowflakeAxesChooserDialog.newInstance(
                        getString(R.string.snowflake_rotation_axes_title)
                    )
                )
                true
            }
    }

    override fun attachObserver() {
        preferenceLiveData.observe(
            viewLifecycleOwner,
            Observer {
                val textureType = TextureProvider.TextureType.SNOWFLAKE_TEXTURE
                if (!it) textureCache.remove(textureType)
                else if (textureCache[textureType] == null) {
                    textureCache[textureType] =
                        TextureProvider.assignDefaultTexture(requireContext(), textureType)
                    preferenceRepository.setSnowflakeTextureSavedPosition(0)
                }

                texturesViewModel.updateTexture(textureType)
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = getString(R.string.snowflake_setup_fragment_title)
    }

    override fun provideBackground(): Drawable? = null

    override fun provideBackgroundColor(): Int = Color.CYAN

    class SnowflakeDialogFragment : TexturedAbstractDialogFragment(
        intArrayOf(
            R.drawable.texture_snowflake_0,
            R.drawable.texture_snowfall_0,
            R.drawable.category_disabled,
            R.drawable.ic_disabled_64dp
        ),
        TextureProvider.TextureType.SNOWFLAKE_TEXTURE
    ) {

        override fun setTextureSavedPosition(position: Int) {
            preferenceRepository.setSnowflakeTextureSavedPosition(position)
        }

        override fun getTextureSavedPosition(): Int =
            preferenceRepository.getSnowflakeTextureSavedPosition()
    }
}
