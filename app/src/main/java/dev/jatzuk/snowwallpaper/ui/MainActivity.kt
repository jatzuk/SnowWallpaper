package dev.jatzuk.snowwallpaper.ui

import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService
import dev.jatzuk.snowwallpaper.ui.preferences.AbstractSimpleDialogFragment
import dev.jatzuk.snowwallpaper.utilities.BackgroundRestrictionNotifier
import dev.jatzuk.snowwallpaper.utilities.Manufacturer

class MainActivity : FragmentActivity() {

    private var isSupportingES2 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            if (PreferenceRepository.getInstance(this)
                    .isUserInformedAboutBackgroundRestrictions()
            ) {
                startLiveWallpaperPreview()
            } else showBackgroundRestrictionInfoDialog()
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
            invokeOnNegativeAction {
                startLiveWallpaperPreview()
            }
        }.show(supportFragmentManager, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == EXIT_ON_SET_REQUEST_CODE) finishAffinity()
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val EXIT_ON_SET_REQUEST_CODE = 99

        fun startLiveWallpaperPreview(context: Context) {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(context, OpenGLWallpaperService::class.java)
                )
            }
            context.startActivity(intent, EXIT_ON_SET_REQUEST_CODE)
        }
    }
}
