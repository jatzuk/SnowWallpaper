package dev.jatzuk.snowwallpaper.views

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.Toast
import dev.jatzuk.snowwallpaper.SnowfallRenderer

class MainActivity : Activity() {
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: SnowfallRenderer
    private var isRendererSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(R.layout.activity_main)
//        button_set_wallpaper.setOnClickListener {
//            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
//                putExtra(
//                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
//                    ComponentName(this@MainActivity, GLWallpaperService::class.java)
//                )
//            }
//            startActivity(intent)
//        }

        renderer = SnowfallRenderer(this)
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val isSupportingES2 = configurationInfo.reqGlEsVersion >= 0x20000

        if (isSupportingES2) {
            glSurfaceView.setEGLContextClientVersion(2)
            glSurfaceView.setRenderer(renderer)
        } else Toast.makeText(
            this,
            "This device does not support OpenGL ES 2.0",
            Toast.LENGTH_LONG
        ).show()

        setContentView(glSurfaceView)
    }

    override fun onPause() {
        super.onPause()
        if (isRendererSet) glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (isRendererSet) glSurfaceView.onResume()
    }
}
