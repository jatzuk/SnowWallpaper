package dev.jatzuk.snowwallpaper.ui.preferences.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import dev.jatzuk.snowwallpaper.R

class IntentPreference(
    context: Context,
    attributeSet: AttributeSet?
) : AbstractPreference(context, attributeSet) {

    init {
        if (backgroundImage == null) {
            backgroundImage = ContextCompat.getDrawable(context, R.drawable.b2)
        }
    }

    override fun setupPreference(view: View) {
        view.run {
            isClickable = true
        }
    }

    override fun provideLayout(): Int = R.layout.preference_intent
}
