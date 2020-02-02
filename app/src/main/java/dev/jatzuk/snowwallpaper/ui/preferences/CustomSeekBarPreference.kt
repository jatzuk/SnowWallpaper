package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.utilities.Logger.logging

class CustomSeekBarPreference(
    context: Context,
    attributeSet: AttributeSet?
) : AbstractPreference(context, attributeSet) {

    override val clickListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            logging("onClick registered inside seekBar - key: $key")
        }

    private lateinit var progressTextView: TextView
    private val seekBarMinValue =
        attributeSet?.getAttributeValue(NAMESPACE, "preferenceMin")?.toInt() ?: 1
    private val seekBarMaxValue =
        attributeSet?.getAttributeValue(NAMESPACE, "preferenceMax")?.toInt() ?: 1

    override fun provideLayout(): Int = R.layout.layout_preference_seekbar

    override fun setupPreference(view: View) {
        view.run {

            progressTextView = findViewById(R.id.progress)
            val progressStartValue = defaultValuePreference?.toInt() ?: 0
            progressTextView.text = "$progressStartValue"

            findViewById<SeekBar>(R.id.seekbar).run {
                progress = progressStartValue
                max = seekBarMaxValue

                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (progress >= seekBarMinValue) updateProgress(progress)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}

                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                })
            }
        }
    }

    private fun updateProgress(progress: Int) {
        progressTextView.text = "$progress"
    }

    companion object {
        private const val TAG = "CustomSeekBarPreference"
    }
}
