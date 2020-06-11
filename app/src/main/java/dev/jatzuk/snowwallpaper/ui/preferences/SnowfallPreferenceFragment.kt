package dev.jatzuk.snowwallpaper.ui.preferences

import android.graphics.Color
import android.graphics.drawable.Drawable
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
                if (!it) textureCache.remove(TextureProvider.TextureType.SNOWFALL_TEXTURE)
                else if (textureCache[TextureProvider.TextureType.SNOWFALL_TEXTURE] == null) {
                    TextureProvider.getDefaultTextureByImageType(
                        requireContext(),
                        TextureProvider.TextureType.SNOWFALL_TEXTURE
                    )
                }
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = getString(R.string.snowfall_setup_fragment_title)
    }

    override fun provideBackground(): Drawable? = null
//        BitmapFactory.decodeResource(resources, R.drawable.b2).toDrawable(resources)

    override fun provideBackgroundColor(): Int = Color.CYAN

    class SnowfallDialogFragment : TexturedAbstractDialogFragment(
        intArrayOf(R.drawable.texture_snowflake, R.drawable.texture_snowfall),
        TextureProvider.TextureType.SNOWFALL_TEXTURE
    ) {

        override fun provideTexturePositionSavePosition(position: Int) {
            preferenceRepository.setSnowfallTextureSavedPosition(position)
        }

        override fun provideTexturePositionLoadPosition(): Int =
            preferenceRepository.getSnowfallTextureSavedPosition()
    }
}
