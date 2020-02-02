package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.Preference
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.utilities.Logger.logging

class CustomSwitchPreference(
    context: Context,
    attributeSet: AttributeSet?
) : AbstractPreference(context, attributeSet) {

    override val clickListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            logging("onClick registered inside switch - key: $key")
        }

    var isChecked = false
        private set // todo private set ?

    override fun setupPreference(view: View) {
        view.run {

            isChecked = defaultValuePreference?.toBoolean() ?: false

            val switcher = findViewById<SwitchCompat>(R.id.switchWidget)
            switcher.isChecked = isChecked
            switcher.setOnClickListener {
                (it as SwitchCompat)
                logging("current switcher value: ${it.isChecked}", TAG)
            }
        }
    }

    override fun provideLayout(): Int = R.layout.layout_preference_switcher

    companion object {
        private const val TAG = "CustomSwitchPreference"
    }
}
