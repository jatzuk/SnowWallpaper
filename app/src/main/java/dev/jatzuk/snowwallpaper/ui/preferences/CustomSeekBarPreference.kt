package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.utilities.Logger.logging

class CustomSeekBarPreference(
    context: Context,
    attributeSet: AttributeSet?
) : AbstractBackgroundPreference(context, attributeSet) {

    override fun provideLayout(): Int = R.layout.layout_preference_seekbar

    override fun setupPreference(view: View) {
        view.run {

            val progressStartValue = defaultValuePreference?.toInt() ?: 0

            val textViewProgress = findViewById<TextView>(R.id.progress)
            textViewProgress.text = "$progressStartValue"

            val seekBar = findViewById<SeekBar>(R.id.seekbar)
            seekBar.progress = progressStartValue
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    logging("progress: $progress", TAG)
                    textViewProgress.text = progress.toString()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        }
    }

    companion object {
        private const val TAG = "CustomSeekBarPreference"
    }
}
