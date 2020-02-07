package dev.jatzuk.snowwallpaper.ui

import android.app.Activity
import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService
import dev.jatzuk.snowwallpaper.opengl.wallpaper.SnowfallRenderer
import dev.jatzuk.snowwallpaper.ui.preferences.PreferencesActivity
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

class MainActivity : Activity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: SnowfallRenderer
    private var isRendererSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startLiveWallpaper()
    }

    override fun onPause() {
        super.onPause()
        if (isRendererSet) glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (isRendererSet) glSurfaceView.onResume()
    }

    private fun startLiveWallpaper() {
        glSurfaceView = GLSurfaceView(this)
        renderer = SnowfallRenderer(this)
//        registerSensorListener() // android wouldn't register the same listener twice
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val isSupportingES2 = configurationInfo.reqGlEsVersion >= 0x20000

        val isBackgroundImageEnabled =
            PreferenceRepository.getInstance(this).getIsBackgroundImageEnabled()

        if (isSupportingES2) {
//            window.setFlags( // todo remove after live wallpaper impl
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
            glSurfaceView.run {
                setEGLContextClientVersion(2)
                if (isBackgroundImageEnabled) {
                    ImageProvider.loadBackgroundImage(this@MainActivity)?.let {
                        scaleBitmap(it)
                        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                        holder.setFormat(PixelFormat.TRANSLUCENT)
                        setZOrderOnTop(true)
                    }
                }
                setRenderer(renderer)
                isRendererSet = true
            }
        } else {
            Toast.makeText(
                this,
                "This device does not support OpenGL ES 2.0",
                Toast.LENGTH_LONG
            ).show()
        }

        setContentView(glSurfaceView)

        demoMode()
    }

    private fun scaleBitmap(bitmap: Bitmap) {
        val scaledBitmap = if (bitmap.width >= bitmap.height) {
            Bitmap.createBitmap(
                bitmap,
                bitmap.width / 2 - bitmap.height / 2,
                0,
                bitmap.height,
                bitmap.height
            )
        } else {
            Bitmap.createBitmap(
                bitmap,
                0,
                bitmap.height / 2 - bitmap.width / 2,
                bitmap.width,
                bitmap.width
            )
        }

        glSurfaceView.background = scaledBitmap.toDrawable(resources)
    }

    private fun demoMode() {
        val buttonsLayout = LayoutInflater.from(this).inflate(
            R.layout.layout_demo, null
        ).apply {
            (this as RelativeLayout).gravity = Gravity.BOTTOM

            findViewById<ImageButton>(R.id.button_open_preferences).setOnClickListener {
                startActivity(Intent(this@MainActivity, PreferencesActivity::class.java))
            }

            findViewById<ImageButton>(R.id.button_set_wallpaper).setOnClickListener {
                setLiveWallpaper()
            }
        }

        addContentView(
            buttonsLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun setLiveWallpaper() {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this@MainActivity, OpenGLWallpaperService::class.java)
            )
        }
        startActivity(intent)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
