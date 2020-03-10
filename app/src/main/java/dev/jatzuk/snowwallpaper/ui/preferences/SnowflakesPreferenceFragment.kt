package dev.jatzuk.snowwallpaper.ui.preferences

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R

@Suppress("unused")
class SnowflakesPreferenceFragment : AbstractPreferenceFragment(R.xml.preferences_snowflake) {

    override fun setUp() {}

    override fun attachObserver() {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value = "Foreground snowflakes"
    }

    override fun provideBackground(): Drawable? = null

    override fun provideBackgroundColor(): Int = Color.CYAN
}
