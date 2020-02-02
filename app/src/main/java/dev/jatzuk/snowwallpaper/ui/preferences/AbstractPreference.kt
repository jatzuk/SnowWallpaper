package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.preference.*
import dev.jatzuk.snowwallpaper.R

abstract class AbstractPreference : Preference {

    private var titleString: String? = null
    private var summaryString: String? = null
    protected var defaultValuePreference: String? = null
    protected abstract val clickListener: SharedPreferences.OnSharedPreferenceChangeListener

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0) {
//        todo("use strings?)
        titleString = attributeSet?.getAttributeValue(NAMESPACE, "preferenceTitle")
        summaryString = attributeSet?.getAttributeValue(NAMESPACE, "preferenceSummary")
        defaultValuePreference =
            attributeSet?.getAttributeValue(NAMESPACE, "preferenceDefaultValue")
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    init {
//        @Suppress("LeakingThis") // todo
        layoutResource = provideLayout()
    }

    override fun onAttached() {
        super.onAttached()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(clickListener)
    }

    override fun onDetached() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(clickListener)
        super.onDetached()
    }

    final override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.isDividerAllowedAbove = false
        holder.isDividerAllowedBelow = false

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
        const val NAMESPACE = "http://schemas.android.com/apk/res-auto"
    }
}
