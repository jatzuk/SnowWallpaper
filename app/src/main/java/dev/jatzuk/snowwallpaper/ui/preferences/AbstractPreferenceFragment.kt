package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.XmlRes
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dev.jatzuk.snowwallpaper.data.preferences.SharedPreferenceRepository
import dev.jatzuk.snowwallpaper.viewmodels.AppBarTitleViewModel

abstract class AbstractPreferenceFragment(
    @XmlRes private val xmlRes: Int
) : PreferenceFragmentCompat(), LifecycleObserver {

    protected abstract val preferencesListener: SharedPreferences.OnSharedPreferenceChangeListener
    protected lateinit var appBarTitleViewModel: AppBarTitleViewModel
    protected lateinit var preferenceRepository: SharedPreferenceRepository

    final override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(xmlRes, rootKey)
        retainInstance = true
        setUp()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.run {
            appBarTitleViewModel = ViewModelProviders.of(this).get(AppBarTitleViewModel::class.java)
            preferenceRepository = SharedPreferenceRepository.getInstance(context!!)
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences
            .registerOnSharedPreferenceChangeListener(preferencesListener)
    }

    final override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(preferencesListener)
    }

    protected abstract fun setUp()

    protected fun switchDependentPreferences(state: Boolean, offset: Int) {
        repeat(preferenceScreen.preferenceCount - offset) { i ->
            val preference = preferenceScreen.getPreference(i + offset)
            preference.isEnabled = state
            if (state && preference is SwitchPreferenceCompat && !preference.isChecked) return
        }
    }
}
