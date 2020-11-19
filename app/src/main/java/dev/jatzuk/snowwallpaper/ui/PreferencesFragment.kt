package dev.jatzuk.snowwallpaper.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Observer
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.ui.preferences.custom.IntentPreference
import dev.jatzuk.snowwallpaper.ui.preferences.fragments.AbstractPreferenceFragment
import dev.jatzuk.snowwallpaper.ui.preferences.fragments.ResetPreferenceDialogFragment
import dev.jatzuk.snowwallpaper.utilities.BackgroundRestrictionNotifier
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

class PreferencesFragment : AbstractPreferenceFragment(R.xml.preferences_main) {

    private lateinit var intentPreferences: Array<IntentPreference>
    private lateinit var categoryDisabledDrawable: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        preferenceRepository = PreferenceRepository.getInstance(requireContext())
        categoryDisabledDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.category_disabled)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = getString(R.string.preferences)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main_preferences_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reset_settings -> showConfirmDialog()
            R.id.restriction_dialog_info -> {
                BackgroundRestrictionNotifier.buildDialog(requireContext())
                    .show(requireActivity().supportFragmentManager, null)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setUp() {
        intentPreferences = arrayOf(
            findPreference(getString(R.string.pref_key_snowfall_category))!!,
            findPreference(getString(R.string.pref_key_snowflake_category))!!,
            findPreference(getString(R.string.pref_key_background_image_category))!!
        )
    }

    override fun attachObserver() {
        texturesViewModel.textures?.observe(
            viewLifecycleOwner,
            Observer {
                intentPreferences.forEachIndexed { index, preference ->
                    val bitmap = it[TextureProvider.TextureType.values()[index]]
                    preference.previewImage =
                        bitmap?.toDrawable(resources) ?: categoryDisabledDrawable
                }
            }
        )
    }

    override fun provideBackgroundColor(): Int =
        ContextCompat.getColor(requireContext(), R.color.colorAccent)

    override fun provideBackground(): Drawable? = null
//        ContextCompat.getDrawable(context!!, R.drawable.background_preferences_main_screen)

    private fun showConfirmDialog(): Boolean {
        ResetPreferenceDialogFragment.newInstance(
            getString(R.string.reset_settings_label),
            getString(R.string.reset_settings_message)
        ).apply {
            invokeOnPositiveAction {
                TextureProvider.clearStoredImages(requireContext())
                preferenceRepository.resetPreferencesToDefault()
                requireActivity().finish()
            }
        }.also { it.show(requireActivity().supportFragmentManager, null) }

        return true
    }

    companion object {
        const val TAG = "PreferencesFragment"
    }
}
