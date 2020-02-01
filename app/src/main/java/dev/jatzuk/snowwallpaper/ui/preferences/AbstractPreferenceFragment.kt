package dev.jatzuk.snowwallpaper.ui.preferences

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.XmlRes
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dev.jatzuk.snowwallpaper.viewmodels.AppBarTitleViewModel

abstract class AbstractPreferenceFragment(
    @XmlRes private val xmlRes: Int
) : PreferenceFragmentCompat() {

    protected lateinit var appBarTitleViewModel: AppBarTitleViewModel

    final override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(xmlRes, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val color = provideBackgroundColor()
        if (color != Color.TRANSPARENT) view?.setBackgroundColor(color)

        val backgroundResId = provideBackground()
        backgroundResId?.let { view?.background = it }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.run {
            appBarTitleViewModel = ViewModelProviders.of(this).get(AppBarTitleViewModel::class.java)
        }
        setUp()
    }

    final override fun onResume() {
        super.onResume()
        attachObserver()
    }

    protected abstract fun setUp()

    protected abstract fun attachObserver()

    @ColorInt
    protected open fun provideBackgroundColor(): Int = Color.TRANSPARENT

    protected open fun provideBackground(): Drawable? = null

    protected fun switchDependentPreferences(state: Boolean, offset: Int) {
        repeat(preferenceScreen.preferenceCount - offset) { i ->
            val preference = preferenceScreen.getPreference(i + offset)
            preference.isEnabled = state
            if (state && preference is SwitchPreferenceCompat && !preference.isChecked) return
        }
    }
}
