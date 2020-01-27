package dev.jatzuk.snowwallpaper.ui.preferences

import android.os.Bundle
import android.view.View
import androidx.annotation.XmlRes
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dev.jatzuk.snowwallpaper.utilities.Logger.logging
import dev.jatzuk.snowwallpaper.viewmodels.AppBarTitleViewModel

abstract class AbstractPreferenceFragment(
    @XmlRes private val xmlRes: Int
) : PreferenceFragmentCompat() {

    protected lateinit var appBarTitleViewModel: AppBarTitleViewModel

    final override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(xmlRes, rootKey)
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachObserver()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        logging("onActivityCreated()")
        activity?.run {
            appBarTitleViewModel = ViewModelProviders.of(this).get(AppBarTitleViewModel::class.java)
        }
        setUp()
    }

    protected abstract fun setUp()

    protected abstract fun attachObserver()

    protected fun switchDependentPreferences(state: Boolean, offset: Int) {
        repeat(preferenceScreen.preferenceCount - offset) { i ->
            val preference = preferenceScreen.getPreference(i + offset)
            preference.isEnabled = state
            if (state && preference is SwitchPreferenceCompat && !preference.isChecked) return
        }
    }
}
