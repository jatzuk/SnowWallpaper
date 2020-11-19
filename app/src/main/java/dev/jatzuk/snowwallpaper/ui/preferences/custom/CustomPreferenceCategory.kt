package dev.jatzuk.snowwallpaper.ui.preferences.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceViewHolder
import dev.jatzuk.snowwallpaper.R

class CustomPreferenceCategory @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int = 0
) : PreferenceGroup(context, attributeSet, defStyleAttr) {

    private var titleString: String? = null

    init {
        context.obtainStyledAttributes(
            attributeSet, R.styleable.AbstractPreference, defStyleAttr, defStyleAttr
        ).use {
            titleString = it.getString(R.styleable.AbstractPreference_preferenceTitle)
        }

        layoutResource = R.layout.preference_category
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.itemView.run {
            findViewById<TextView>(R.id.category_name).apply {
                text = titleString ?: context.getString(R.string.no_title)
                background =
                    ContextCompat.getDrawable(context, R.drawable.category_preference_background)
            }
        }
    }
}
