package dev.jatzuk.snowwallpaper.utilities

import android.content.Context
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.ui.preferences.fragments.AbstractSimpleDialogFragment

class BackgroundRestrictionNotifier : AbstractSimpleDialogFragment() {

    companion object {
        fun buildDialog(context: Context) = BackgroundRestrictionNotifier().apply {
            arguments = Bundle().apply {
                putString(
                    TITLE_ARGUMENT_KEY,
                    context.getString(R.string.manufacturer_background_restrictions_title)
                )
                putString(
                    MESSAGE_ARGUMENT_KEY,
                    context.getString(R.string.manufacturer_background_restrictions_message)
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        PreferenceRepository.getInstance(requireContext())
            .setIsUserInformedAboutBackgroundRestrictions(true)
    }
}
