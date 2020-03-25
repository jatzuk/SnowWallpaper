package dev.jatzuk.snowwallpaper.ui.preferences.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.ui.helpers.CircleImageView

class IntentPreference(
    context: Context,
    attributeSet: AttributeSet?
) : AbstractPreference(context, attributeSet) {

    var previewImage: Drawable? = null
        set(value) {
            updatePreview()
            field = value
        }
    private var circleImageView: CircleImageView? = null

    init {
        if (backgroundImage == null) {
            backgroundImage =
                ContextCompat.getDrawable(context, R.drawable.background_preference_intent)
        }
    }

    override fun setupPreference(view: View) {
        view.run {
            circleImageView = findViewById(R.id.circle_image_view)!!
            if (previewImage != null) setPreviewImage()
            else circleImageView!!.visibility = View.GONE

            isClickable = true
        }
    }

    private fun updatePreview() {
        circleImageView?.let {
            if (previewImage != null) setPreviewImage()
            else disablePreviewImage()
        }
    }

    private fun setPreviewImage() {
        circleImageView?.apply {
            setPreviewImage(
                previewImage!!,
                resources.getDimensionPixelSize(R.dimen.intent_preview_image_size)
            )
            setStroke(
                resources.getDimensionPixelOffset(R.dimen.intent_preview_image_stroke_width),
                Color.BLACK
            )
            visibility = View.VISIBLE
            invalidate()
        }
    }

    private fun disablePreviewImage() {
        circleImageView?.visibility = View.INVISIBLE
    }

    override fun provideLayout(): Int = R.layout.preference_intent
}
