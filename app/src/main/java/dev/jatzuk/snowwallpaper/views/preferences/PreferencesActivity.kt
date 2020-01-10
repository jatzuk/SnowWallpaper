package dev.jatzuk.snowwallpaper.views.preferences

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dev.jatzuk.snowwallpaper.R
import java.io.File
import java.io.FileOutputStream

class PreferencesActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.preferences_container, PreferencesFragment())
            .commit()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                SELECT_CUSTOM_BACKGROUND_IMAGE -> {
                    data?.let {
                        val inp = applicationContext.contentResolver.openInputStream(it.data!!)
                        val bitmap = BitmapFactory.decodeStream(inp)
                        val name = bitmap.config.name
                        FileOutputStream(File(name)).use { fos ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                        }
                    } ?: return
                }
                SELECT_PREDEFINED_BACKGROUND_IMAGE -> {

                }
            }
        }
    }

    companion object {
        private const val TAG = "PreferencesActivity"
        private const val SELECT_PREDEFINED_BACKGROUND_IMAGE = 1
        private const val SELECT_CUSTOM_BACKGROUND_IMAGE = 2
    }

    class PreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_main, rootKey)
        }
    }
}
