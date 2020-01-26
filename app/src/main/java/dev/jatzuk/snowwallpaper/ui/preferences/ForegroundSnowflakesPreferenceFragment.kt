package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.SharedPreferences
import android.os.Bundle
import dev.jatzuk.snowwallpaper.R

@Suppress("unused")
class ForegroundSnowflakesPreferenceFragment :
    AbstractPreferenceFragment(R.xml.preferences_foreground_snowflakes) {

    override val preferencesListener: SharedPreferences.OnSharedPreferenceChangeListener
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        todo(title bar name)
    }

    override fun setUp() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
