package dev.jatzuk.snowwallpaper.ui.preferences.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import dev.jatzuk.snowwallpaper.R

class IntentPreference(
    context: Context,
    attributeSet: AttributeSet?
) : AbstractPreference(context, attributeSet) {

    private var previewImage: Drawable? = null

    init {
        if (backgroundImage == null) {
            backgroundImage =
                ContextCompat.getDrawable(context, R.drawable.background_preference_intent)
        }

//        todo load previewImage
    }

    override fun setupPreference(view: View) {

        view.run {
            findViewById<ImageView>(R.id.image_view).run {
                visibility = if (previewImage == null) View.GONE else View.VISIBLE
            }

            isClickable = true
        }
    }

    override fun provideLayout(): Int = R.layout.preference_intent
}
