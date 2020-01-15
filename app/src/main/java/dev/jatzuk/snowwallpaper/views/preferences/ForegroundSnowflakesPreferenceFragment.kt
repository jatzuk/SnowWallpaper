package dev.jatzuk.snowwallpaper.views.preferences

import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.util.AppBarTitleViewModel

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
