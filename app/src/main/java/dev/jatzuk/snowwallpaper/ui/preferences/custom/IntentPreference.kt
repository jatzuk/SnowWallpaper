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

    private var previewImage: Drawable? = null

    init {
        if (backgroundImage == null) {
//            backgroundImage =
//                ContextCompat.getDrawable(context, R.drawable.background_preference_intent)
        }

//        todo load previewImage
    }

    override fun setupPreference(view: View) {

        view.run {
            val drawableRes = when (key) { // todo replace keys
                context.getString(R.string.background_snowflakes_category_key) -> {
                    R.drawable.texture_snowfall
                }
                context.getString(R.string.foreground_snowflakes_category_key) -> {
                    R.drawable.texture_snowflake
                }
                context.getString(R.string.background_image_global_switcher_key) -> {
                    R.drawable.b0
                }
                else -> -1
            }

            val circleImageView = findViewById<CircleImageView>(R.id.circle_image_view)
            if (drawableRes != -1) {
                previewImage = ContextCompat.getDrawable(context, drawableRes)
                circleImageView.apply {
                    setPreviewImage(
                        previewImage!!,
                        resources.getDimensionPixelSize(R.dimen.intent_preview_image_size)
                    )
                    setStroke(
                        resources.getDimensionPixelOffset(R.dimen.intent_preview_image_stroke_width),
                        Color.BLACK
                    )
                    visibility = View.VISIBLE
                }
            } else circleImageView.visibility = View.GONE

            isClickable = true
        }
    }

    override fun provideLayout(): Int = R.layout.preference_intent
}
