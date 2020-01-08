package dev.jatzuk.snowwallpaper.views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.SnowfallRenderer
import dev.jatzuk.snowwallpaper.util.Logger.logging
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

class MainActivity : Activity() {
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorEventListener: SensorEventListener
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: SnowfallRenderer
    private var isRendererSet = false
    private var orientation = Configuration.ORIENTATION_PORTRAIT

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

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = it.values[0]
                    val y = it.values[1]

                    roll =
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE && x > 0)
                            -calculateRoll(y, x)
                        else calculateRoll(x, y)

                    val absoluteY = abs(y) / 10_000f
                    if (absoluteY > 0.5f) pitch = absoluteY
                    logging("pitch: $absoluteY", SENSOR_INFO_TAG)
                }
            }
        }

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
            isRendererSet = true
            glSurfaceView.run {
                setEGLContextClientVersion(2)
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                holder.setFormat(PixelFormat.TRANSLUCENT)
                setBackgroundResource(loadBackgroundImage())
                setZOrderOnTop(true)
                setRenderer(renderer)
            }
        } else Toast.makeText(
            this,
            "This device does not support OpenGL ES 2.0",
            Toast.LENGTH_LONG
        ).show()

        setContentView(glSurfaceView)
    }

    override fun onPause() {
        super.onPause()
        if (isRendererSet) {
            sensorManager.unregisterListener(sensorEventListener)
            glSurfaceView.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isRendererSet) {
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(
                sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            glSurfaceView.onResume()
        }
    }

    private fun calculateRoll(x: Float, y: Float) = (atan2(x, y) / PI / 180).toFloat()

    private fun loadBackgroundImage() = R.drawable.background_image // todo(load from prefs)

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        orientation = newConfig.orientation
    }

    companion object {
        private const val SENSOR_INFO_TAG = "SENSOR_INFO"
        private val TAG = MainActivity::class.java.simpleName
        var ratio = 0f
        var roll = 0f
        var pitch = 0f
    }
}
