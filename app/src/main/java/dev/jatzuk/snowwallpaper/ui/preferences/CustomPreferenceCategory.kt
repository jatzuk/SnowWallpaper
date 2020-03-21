package dev.jatzuk.snowwallpaper.ui.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.res.use
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceViewHolder
import dev.jatzuk.snowwallpaper.R

class CustomPreferenceCategory : PreferenceGroup {

    private var titleString: String? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    @SuppressLint("Recycle")
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        context.obtainStyledAttributes(
            attributeSet, R.styleable.AbstractPreference, defStyleAttr, defStyleAttr
        ).use {
            titleString = it.getString(R.styleable.AbstractPreference_preferenceTitle)
        }
    }

    init {
        layoutResource = R.layout.preference_category
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.itemView.run {
            findViewById<TextView>(R.id.category_name).apply {
                text = titleString ?: "no title provided" // todo replace str
//                background = ContextCompat.getDrawable(context, R.drawable.background_preference_category)
                setBackgroundResource(R.color.colorPreferenceCategoryBackground) // todo tmp
            }
        }
    }
}
