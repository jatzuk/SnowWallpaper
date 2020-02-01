package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.utilities.Logger.logging

class CustomSwitchPreference(
    context: Context,
    attributeSet: AttributeSet?
) : AbstractBackgroundPreference(context, attributeSet) {

    override fun setupPreference(view: View) {
        view.run {

            val isChecked = defaultValuePreference?.toBoolean() ?: false

            val switcher = findViewById<SwitchCompat>(R.id.switchWidget)
            switcher.isChecked = isChecked
            switcher.setOnClickListener {
                (it as SwitchCompat)
                logging("current switcher value: ${it.isChecked}")
            }
        }
    }

    override fun provideLayout(): Int = R.layout.layout_preference_switcher
}
