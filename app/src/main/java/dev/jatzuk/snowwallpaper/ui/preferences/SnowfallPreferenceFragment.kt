package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toDrawable
import androidx.preference.SwitchPreferenceCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository

@Suppress("unused")
class SnowfallPreferenceFragment : AbstractPreferenceFragment(R.xml.preferences_snowfall) {

    private val preferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            val isEnabled = PreferenceRepository.PREF_KEY_IS_SNOWFALL_ENABLED
            val isRandomRadius = PreferenceRepository.PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED

            when (key) {
                isEnabled -> {
                    val state = sharedPreferences.getBoolean(isEnabled, true)
                    switchDependentPreferences(state, 1)
                }
                isRandomRadius -> {
                    val state = sharedPreferences.getBoolean(isRandomRadius, true)
                    switchDependentPreferences(state, 4)
                }
            }
        }

    override fun setUp() {
        val isCategoryEnabled = findPreference<SwitchPreferenceCompat>(
            PreferenceRepository.PREF_KEY_IS_SNOWFALL_ENABLED
        )!!.isChecked
        switchDependentPreferences(isCategoryEnabled, 1)

        if (isCategoryEnabled) {
            val isUniqueRadiusChecked = findPreference<SwitchPreferenceCompat>(
                PreferenceRepository.PREF_KEY_IS_SNOWFALL_UNIQUE_RADIUS_ENABLED
            )!!.isChecked
            switchDependentPreferences(isUniqueRadiusChecked, 4)
        }
    }

    override fun attachObserver() {
        preferenceManager.sharedPreferences
            .registerOnSharedPreferenceChangeListener(preferencesListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarTitleViewModel.title.value =
            getString(R.string.background_snowflakes) // todo(change attrib name)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(preferencesListener)
    }

//    override fun provideBackground(): Drawable? =
//        BitmapFactory.decodeResource(resources, R.drawable.b2).toDrawable(resources)

    override fun provideBackgroundColor(): Int = Color.CYAN

    override fun onCreateRecyclerView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): RecyclerView {
        val recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState)
        recyclerView.addItemDecoration(Divider(50))
        return recyclerView
    }

    private inner class Divider(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
//            if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1)
            outRect.apply {
                bottom = spaceHeight
                left = spaceHeight / 2
                right = spaceHeight / 2
            }
        }
    }
}
