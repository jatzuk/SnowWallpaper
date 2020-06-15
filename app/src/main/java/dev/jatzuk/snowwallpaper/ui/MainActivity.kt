package dev.jatzuk.snowwallpaper.ui

import android.app.Activity
import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService

class MainActivity : Activity() {

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
        } else startLiveWallpaperPreview()
    }

    private fun startLiveWallpaperPreview() {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this@MainActivity, OpenGLWallpaperService::class.java)
            )
        }
        startActivity(intent)
    }
}
