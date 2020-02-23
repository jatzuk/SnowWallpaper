package dev.jatzuk.snowwallpaper.ui.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.res.use
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.utilities.Logger.logging

abstract class AbstractPreference : Preference {

    private var titleString: String? = null
    private var summaryString: String? = null
    protected var defaultValuePreference: String? = null
    protected val preferenceRepository = PreferenceRepository.getInstance(context)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    @SuppressLint("Recycle") // recycled via use extension function
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        context.obtainStyledAttributes(
            attributeSet, R.styleable.AbstractPreference, defStyleAttr, defStyleAttr
        ).use {
            titleString = it.getString(R.styleable.AbstractPreference_preferenceTitle)
            summaryString = it.getString(R.styleable.AbstractPreference_preferenceSummary)
            defaultValuePreference =
                it.getString(R.styleable.AbstractPreference_preferenceDefaultValue)
        }
    }

    init {
//        @Suppress("LeakingThis") // todo
        layoutResource = provideLayout()
    }

    final override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.apply {
            isDividerAllowedAbove = false
            isDividerAllowedBelow = false
        }

        holder.itemView.run {
            findViewById<TextView>(R.id.title).text = titleString ?: "no title provided" // todo
            findViewById<TextView>(R.id.summary).text = summaryString ?: "no summary provided"//todo

            isClickable = false
            setupPreference(this)
        }
    }

    override fun shouldDisableDependents(): Boolean = when (key) {
        PreferenceRepository.PREF_KEY_IS_SNOWFALL_ENABLED -> {
            !preferenceRepository.getIsSnowfallEnabled()
        }
        PreferenceRepository.PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED -> {
            !preferenceRepository.getIsSnowfallUniqueRadiusEnabled()
        }
        PreferenceRepository.PREF_KEY_IS_BACKGROUND_IMAGE_ENABLED -> {
            !preferenceRepository.getIsBackgroundImageEnabled()
        }
        else -> super.shouldDisableDependents()
    }

    override fun onDependencyChanged(dependency: Preference, disableDependent: Boolean) {
        isVisible = when (key) {
            PreferenceRepository.PREF_KEY_SNOWFALL_MIN_RADIUS,
            PreferenceRepository.PREF_KEY_SNOWFALL_MAX_RADIUS -> {
                preferenceRepository.getIsSnowfallEnabled() &&
                        preferenceRepository.getIsSnowfallUniqueRadiusEnabled()
            }
            else -> !disableDependent
        }
        super.onDependencyChanged(dependency, disableDependent)
    }

    abstract fun setupPreference(view: View)

    @LayoutRes
    abstract fun provideLayout(): Int

    companion object {
        const val NAMESPACE = "http://schemas.android.com/apk/res-auto"
    }
}