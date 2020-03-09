package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository

class CustomSeekBarPreference(
    context: Context,
    attributeSet: AttributeSet?
) : AbstractPreference(context, attributeSet) {

    private lateinit var progressTextView: TextView
    private lateinit var seekBar: SeekBar
    private val seekBarMinValue =
        attributeSet?.getAttributeValue(NAMESPACE, PREFERENCE_MIN)!!.toInt()
    private val seekBarMaxValue =
        attributeSet?.getAttributeValue(NAMESPACE, PREFERENCE_MAX)!!.toInt()
    private var currentProgress = initPropertyValue()

    override fun provideLayout(): Int = R.layout.layout_preference_seekbar

    override fun setupPreference(view: View) {
        view.run {
            progressTextView = findViewById(R.id.progress)

            seekBar = findViewById(R.id.seekbar)
            seekBar.run {
                max = seekBarMaxValue - seekBarMinValue
                progress = currentProgress - seekBarMinValue
                updateProgressTextView()

                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser) currentProgress = seekBarMinValue + progress
                        updateProgressTextView()
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        sharedPreferences.edit().putInt(key, currentProgress).apply()
                    }
                })
            }
        }
    }

    private fun initPropertyValue(): Int = when (key) {
        PreferenceRepository.PREF_KEY_SNOWFALL_LIMIT -> {
            preferenceRepository.getSnowfallLimit()
        }
        PreferenceRepository.PREF_KEY_SNOWFALL_VELOCITY_FACTOR -> {
            preferenceRepository.getSnowfallVelocityFactor()
        }
        PreferenceRepository.PREF_KEY_SNOWFALL_MIN_RADIUS -> {
            preferenceRepository.getSnowfallMinRadius().toInt()
        }
        PreferenceRepository.PREF_KEY_SNOWFALL_MAX_RADIUS -> {
            preferenceRepository.getSnowfallMaxRadius().toInt()
        }

        PreferenceRepository.PREF_KEY_SNOWFLAKE_LIMIT -> {
            preferenceRepository.getSnowflakeLimit()
        }
        PreferenceRepository.PREF_KEY_SNOWFLAKE_VELOCITY_FACTOR -> {
            preferenceRepository.getSnowflakeVelocityFactor()
        }
        PreferenceRepository.PREF_KEY_SNOWFLAKE_ROTATION_VELOCITY -> {
            preferenceRepository.getSnowflakeRotationVelocity()
        }
        PreferenceRepository.PREF_KEY_SNOWFLAKE_MIN_RADIUS -> {
            preferenceRepository.getSnowflakeMinRadius()
        }
        PreferenceRepository.PREF_KEY_SNOWFLAKE_MAX_RADIUS -> {
            preferenceRepository.getSnowflakeMaxRadius()
        }
        else -> defaultValuePreference!!.toInt()
    }

    private fun updateProgressTextView() {
        progressTextView.text = "$currentProgress"
    }

    companion object {
        private const val TAG = "CustomSeekBarPreference"
        private const val PREFERENCE_MIN = "preferenceMin"
        private const val PREFERENCE_MAX = "preferenceMax"
    }
}
