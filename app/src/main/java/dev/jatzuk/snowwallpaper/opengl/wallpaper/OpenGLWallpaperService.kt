package dev.jatzuk.snowwallpaper.opengl.wallpaper

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.utilities.ImageProvider
import kotlin.math.PI
import kotlin.math.atan2

class OpenGLWallpaperService : WallpaperService() {

    private lateinit var sensorManager: SensorManager
    private lateinit var sensorEventListener: SensorEventListener
    private var orientation = Configuration.ORIENTATION_PORTRAIT

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = it.values[0]
                    val y = it.values[1]

                    roll =
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE && x > 0)
                            -calculateRoll(y, x)
                        else calculateRoll(x, y)
                }
            }
        }
    }

    override fun onCreateEngine(): Engine = WallpaperEngine()

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        orientation = newConfig.orientation
    }

    private fun calculateRoll(x: Float, y: Float) = (atan2(x, y) / PI / 180).toFloat()

    private inner class WallpaperEngine : Engine() {

        private lateinit var glSurfaceView: WallpaperGLSurfaceView
        private lateinit var renderer: SnowfallRenderer
        private var isRendererSet = false

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

            registerSensorListener()

            glSurfaceView = WallpaperGLSurfaceView(this@OpenGLWallpaperService)
            renderer = SnowfallRenderer(this@OpenGLWallpaperService)

            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val configurationInfo = activityManager.deviceConfigurationInfo
            val ifSupportsES2 = configurationInfo.reqGlEsVersion >= 0x20000

            val isBackgroundImageEnabled =
                PreferenceRepository.getInstance(this@OpenGLWallpaperService)
                    .getIsBackgroundImageEnabled()

            if (ifSupportsES2) {
                glSurfaceView.run {
                    setEGLContextClientVersion(2)
                    preserveEGLContextOnPause = true

                    if (isBackgroundImageEnabled) {
                        ImageProvider.loadBackgroundImage(this@OpenGLWallpaperService)!!.let {
                            scaleBitmap(it)
                            setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                            holder.setFormat(PixelFormat.TRANSLUCENT)
                            setZOrderOnTop(true)
                        }
                    }

                    setRenderer(SnowfallRenderer(this@OpenGLWallpaperService))
                    isRendererSet = true
                }
            } else {
                Toast.makeText(
                    this@OpenGLWallpaperService,
                    "This device does not support OpenGL ES 2.0",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (isRendererSet) {
                if (visible) glSurfaceView.onResume()
                else glSurfaceView.onPause()
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            glSurfaceView.onWallpaperDestroy()
        }

        private fun registerSensorListener() {
            sensorManager.registerListener(
                sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
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


        private inner class WallpaperGLSurfaceView(context: Context) : GLSurfaceView(context) {

            override fun getHolder(): SurfaceHolder = surfaceHolder

            fun onWallpaperDestroy() {
                super.onDetachedFromWindow()
            }
        }
    }

    companion object {
        private const val SENSOR_INFO_TAG = "SENSOR_INFO_TAG"
        var ratio = 0f
        var roll = 0f
    }
}
