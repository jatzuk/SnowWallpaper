package dev.jatzuk.snowwallpaper.ui

import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService
import dev.jatzuk.snowwallpaper.utilities.BackgroundRestrictionNotifier
import dev.jatzuk.snowwallpaper.utilities.Manufacturer

class MainActivity : FragmentActivity() {

    private var isSupportingES2 = false
    private lateinit var preferenceRepository: PreferenceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceRepository = PreferenceRepository.getInstance(this)
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        isSupportingES2 = configurationInfo.reqGlEsVersion >= 0x20000
    }

    override fun onResume() {
        super.onResume()

        if (!isSupportingES2) {
            Toast.makeText(
                this,
                getString(R.string.open_gl20_unsupported_device_info),
                Toast.LENGTH_LONG
            ).show()
            finish()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                !preferenceRepository.isUserInformedAboutBackgroundRestriction()
            ) {
                showBackgroundRestrictionInfoDialog()
            } else {
                startLiveWallpaperPreview()
            }
        }
    }

    private fun startLiveWallpaperPreview() {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this@MainActivity, OpenGLWallpaperService::class.java)
            )
        }
        startActivityForResult(intent, EXIT_ON_SET_REQUEST_CODE)
    }

    private fun showBackgroundRestrictionInfoDialog() {
        BackgroundRestrictionNotifier.buildDialog(this).apply {
            invokeOnPositiveAction {
                Manufacturer.tryOpenManufacturerBackgroundRestrictionSettings(this@MainActivity)
            }
            invokeOnNegativeAction { startLiveWallpaperPreview() }
        }.show(supportFragmentManager, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == EXIT_ON_SET_REQUEST_CODE) finish()
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val EXIT_ON_SET_REQUEST_CODE = 99
    }
}
