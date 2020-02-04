package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import dev.jatzuk.snowwallpaper.R

class CustomSeekBarPreference(
    context: Context,
    attributeSet: AttributeSet?
) : AbstractPreference(context, attributeSet) {

    private lateinit var progressTextView: TextView
    private val seekBarMinValue =
        attributeSet?.getAttributeValue(NAMESPACE, PREFERENCE_MIN)?.toInt() ?: 1
    private val seekBarMaxValue =
        attributeSet?.getAttributeValue(NAMESPACE, PREFERENCE_MAX)?.toInt() ?: 30

    override fun provideLayout(): Int = R.layout.layout_preference_seekbar

    override fun setupPreference(view: View) {
        view.run {

            progressTextView = findViewById(R.id.progress)
            val progressStartValue = defaultValuePreference?.toInt() ?: 0 // todo pref value ?: def
            updateProgress(progressStartValue)

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

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        sharedPreferences.edit().putInt(key, seekBar.progress).apply()
                    }
                })
            }
        }
    }

    private fun updateProgress(progress: Int) {
        progressTextView.text = "$progress"
    }

    companion object {
        private const val TAG = "CustomSeekBarPreference"
        private const val PREFERENCE_MIN = "preferenceMin"
        private const val PREFERENCE_MAX = "preferenceMax"
    }
}
