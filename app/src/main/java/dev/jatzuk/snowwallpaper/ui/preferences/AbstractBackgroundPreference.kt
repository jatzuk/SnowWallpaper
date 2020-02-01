package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.preference.*
import dev.jatzuk.snowwallpaper.R

abstract class AbstractBackgroundPreference : Preference {

    private var titleString: String? = null
    private var summaryString: String? = null
    protected var defaultValuePreference: String? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0) {
//        todo("use strings?)
        titleString = attributeSet?.getAttributeValue(NAMESPACE, "preferenceTitle")
        summaryString = attributeSet?.getAttributeValue(NAMESPACE, "preferenceSummary")
        defaultValuePreference = attributeSet?.getAttributeValue(NAMESPACE, "preferenceDefaultValue")
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    init {
//        @Suppress("LeakingThis")
        layoutResource = provideLayout()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    final override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.itemView.run {
            val title = findViewById<TextView>(R.id.title)
            title.text = titleString ?: "no title provided" // todo

            val summary = findViewById<TextView>(R.id.summary)
            summary.text = summaryString ?: "no summary provided" // todo

            isClickable = false
            setupPreference(this)
        }
    }

    abstract fun setupPreference(view: View)

    @LayoutRes
    abstract fun provideLayout(): Int

    companion object {
        private const val NAMESPACE = "http://schemas.android.com/apk/res-auto"
    }
}
