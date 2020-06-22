package dev.jatzuk.snowwallpaper.ui.preferences

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.viewmodels.AppBarTitleViewModel

class PreferencesActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private lateinit var appBarTitleViewModel: AppBarTitleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.preferences_container,
                    PreferencesFragment(),
                    PreferencesFragment::class.java.simpleName
                )
                .commit()
        }

        appBarTitleViewModel = ViewModelProvider(this).get(AppBarTitleViewModel::class.java)
        appBarTitleViewModel.title.observe(this, Observer {
            supportActionBar?.title = it
        })
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?,
        pref: Preference
    ): Boolean {
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment
        )
        fragment.apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .handlePreferenceStartFragmentPosition(fragment)
            .commit()
        return true
    }

    private fun FragmentTransaction.handlePreferenceStartFragmentPosition(
        fragment: Fragment
    ): FragmentTransaction {
        val container = findViewById<FrameLayout>(R.id.preferences_settings_container)
            ?: findViewById(R.id.preferences_container)

        return apply {
            replace(container.id, fragment)
            if (container.id != R.id.preferences_settings_container) addToBackStack(null)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
