package dev.jatzuk.snowwallpaper.ui.preferences

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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

        val retainFragment = PreferencesFragment.findOrCreateRetainFragment(supportFragmentManager)

        supportFragmentManager.beginTransaction()
            .replace(R.id.preferences_container, retainFragment, PreferencesFragment.TAG)
            .commit()

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
            .replace(R.id.preferences_container, fragment)
            .addToBackStack(null)
            .commit()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "PreferencesActivity"
    }
}
