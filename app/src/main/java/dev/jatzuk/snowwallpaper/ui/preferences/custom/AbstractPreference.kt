package dev.jatzuk.snowwallpaper.ui.preferences.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.res.use
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository

abstract class AbstractPreference : Preference {

    private var titleString: String? = null
    protected var summaryString: String? = null
    protected var summaryStringDefault: String? = null
    protected var defaultValuePreference: Int? = null
    protected var backgroundImage: Drawable? = null
    protected val preferenceRepository = PreferenceRepository.getInstance(context)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    @SuppressLint("Recycle") // recycled via "use" extension function
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
            summaryStringDefault = summaryString
            defaultValuePreference =
                it.getInteger(R.styleable.AbstractPreference_preferenceDefaultValue, 0)
            backgroundImage = it.getDrawable(R.styleable.AbstractPreference_preferenceBackground)
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
            backgroundImage?.let { findViewById<ViewGroup>(R.id.master_layout).background = it }

            isClickable = false
            setupPreference(this)

            findViewById<TextView>(R.id.title).text = titleString ?: "no title provided" // todo
            findViewById<TextView>(R.id.summary).text = summaryString ?: "no summary provided"//todo
        }
    }

    override fun shouldDisableDependents(): Boolean = when (key) {
        PreferenceRepository.PREF_KEY_IS_SNOWFALL_ENABLED -> {
            !preferenceRepository.getIsSnowfallEnabled()
        }
        PreferenceRepository.PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED -> {
            !preferenceRepository.getIsSnowfallUniqueRadiusEnabled()
        }

        PreferenceRepository.PREF_KEY_IS_SNOWFLAKE_ENABLED -> {
            !preferenceRepository.getIsSnowflakeEnabled()
        }
        PreferenceRepository.PREF_KEY_IS_SNOWFLAKE_UNIQUE_RADIUS_ENABLED -> {
            !preferenceRepository.getIsSnowflakeUniqueRadiusEnabled()
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
            PreferenceRepository.PREF_KEY_SNOWFLAKE_MIN_RADIUS,
            PreferenceRepository.PREF_KEY_SNOWFLAKE_MAX_RADIUS -> {
                preferenceRepository.getIsSnowflakeEnabled() &&
                        preferenceRepository.getIsSnowflakeUniqueRadiusEnabled()
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