package dev.jatzuk.snowwallpaper.utilities

import android.content.Context
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.ui.preferences.AbstractSimpleDialogFragment

class BackgroundRestrictionNotifier private constructor() : AbstractSimpleDialogFragment() {

    companion object {
        fun buildDialog(context: Context) = BackgroundRestrictionNotifier().apply {
            val preferenceRepository = PreferenceRepository.getInstance(context)
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

            invokeOnPositiveAction {
                Manufacturer.tryOpenManufacturerBackgroundRestrictionSettings(requireContext())
                preferenceRepository.setIsUserInformedAboutBackgroundRestrictions(true)

            }
            invokeOnNegativeAction {
                preferenceRepository.setIsUserInformedAboutBackgroundRestrictions(true)
            }
        }
    }

}
