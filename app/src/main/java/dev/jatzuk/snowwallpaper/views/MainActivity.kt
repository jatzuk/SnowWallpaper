package dev.jatzuk.snowwallpaper.views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import dev.jatzuk.snowwallpaper.SnowfallRenderer

class MainActivity : Activity() {
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: SnowfallRenderer
    private var isRendererSet = false
    private var previousX = 0f
    private var previousY = 0f

    @SuppressLint("ClickableViewAccessibility")
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
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        glSurfaceView = GLSurfaceView(this)
        renderer = SnowfallRenderer(this)
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val isSupportingES2 = configurationInfo.reqGlEsVersion >= 0x20000

        if (isSupportingES2) {
            glSurfaceView.setEGLContextClientVersion(2)
            isRendererSet = true
            glSurfaceView.setRenderer(renderer)
        } else Toast.makeText(
            this,
            "This device does not support OpenGL ES 2.0",
            Toast.LENGTH_LONG
        ).show()

        glSurfaceView.setOnTouchListener { v, event ->
            val x = event.x
            val y = event.y

            event?.let {
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        val dx = x - previousX
                        val dy = y - previousY
                        previousX = x
                        previousY = y
                        glSurfaceView.queueEvent { renderer.handleTouchDrag(dx, dy) }
                    }
                }
                true
            } ?: false
        }

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
