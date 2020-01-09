package dev.jatzuk.snowwallpaper.views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.core.graphics.drawable.toDrawable
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.SnowfallRenderer
import dev.jatzuk.snowwallpaper.util.Logger.logging
import kotlinx.android.synthetic.main.activity_main.*
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

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_main)

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

        button_open_preferences.setOnClickListener {
            startActivity(Intent(this, PreferencesActivity::class.java))
        }

        button_set_wallpaper.setOnClickListener {
            startLiveWallpaper()
        }

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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        orientation = newConfig.orientation
    }

    private fun startLiveWallpaper() {
        glSurfaceView = GLSurfaceView(this)
        renderer = SnowfallRenderer(this)
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val isSupportingES2 = configurationInfo.reqGlEsVersion >= 0x20000

        scaleBitmap()

        if (isSupportingES2) {
            isRendererSet = true
            glSurfaceView.run {
                setEGLContextClientVersion(2)
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                holder.setFormat(PixelFormat.TRANSLUCENT)
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

    private fun scaleBitmap() {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.background_image)
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


//        val getIntent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
//        val pickIntent = Intent(
//            Intent.ACTION_PICK,
//            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        ).apply { type = "image/*" }
//
//        val chooserIntent = Intent.createChooser(getIntent, "Select Background Picture").apply {
//            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
//        }
//
//        startActivityForResult(chooserIntent, SELECT_BACKGROUND_IMAGE)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == RESULT_OK) {
//            when (requestCode) {
//                SELECT_BACKGROUND_IMAGE -> {
//                    data?.let {
//                        val inp = applicationContext.contentResolver.openInputStream(it.data!!)
//                        val bitmap = BitmapFactory.decodeStream(inp)
//                        val name = bitmap.config.name
//                        FileOutputStream(File(name)).use { fos ->
//                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
//                        }
//                    } ?: return
//                }
//            }
//        }
//    }


    companion object {
        private const val SENSOR_INFO_TAG = "SENSOR_INFO"
        private val TAG = MainActivity::class.java.simpleName
//        private const val SELECT_BACKGROUND_IMAGE = 1

        var ratio = 0f
        var roll = 0f
        var pitch = 0f
    }
}
