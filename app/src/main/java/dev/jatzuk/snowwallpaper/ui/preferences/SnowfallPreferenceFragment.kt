package dev.jatzuk.snowwallpaper.ui.preferences

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R

@Suppress("unused")
class SnowfallPreferenceFragment : AbstractPreferenceFragment(R.xml.preferences_snowfall) {

    override fun setUp() {}

    override fun attachObserver() {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value =
            getString(R.string.background_snowflakes) // todo(change attrib name)
    }

    override fun provideBackground(): Drawable? = null
//        BitmapFactory.decodeResource(resources, R.drawable.b2).toDrawable(resources)

    override fun provideBackgroundColor(): Int = Color.CYAN
}
