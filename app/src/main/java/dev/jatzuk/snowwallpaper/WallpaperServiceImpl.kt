package dev.jatzuk.snowwallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class WallpaperServiceImpl : WallpaperService() {
    override fun onCreateEngine(): Engine = WallpaperServiceEngineImpl()

    private inner class WallpaperServiceEngineImpl : Engine() {
        private val handler = Handler()
        private val runnable: Runnable = Runnable { update() }
        private lateinit var bitmap: Bitmap
        private lateinit var flakes: Array<Snowflake>
        private lateinit var sensorManager: SensorManager

        private val sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]

                val z = event.values[2]
                val a = sqrt(x.pow(2f) + y.pow(2f) + z.pow(2f))

                val angle = atan2(x, y) / (PI / 180)
                if (angle in -MAX_ANGLE..MAX_ANGLE) roll =
                    angle.toFloat() // todo("check condition")
                log("azimuth: $roll")
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            log("onSurfaceChanged")
            WallpaperServiceImpl.width = width
            WallpaperServiceImpl.height = height
            bitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.background
                ), width, height, false
            )
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(
                sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            flakes = Array(FLAKES_COUNT) { Snowflake() }
            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            log("onVisibilityChanged")
            if (visible) {
                log("onVisibilityChanged - VISIBLE")
                handler.post(runnable)
            } else handler.removeCallbacks(runnable)
            super.onVisibilityChanged(visible)
        }

        private fun update() {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder?.lockCanvas()
                canvas?.let { c ->
                    c.drawColor(Color.BLACK)
//                    c.drawBitmap(bitmap, 0f, 0f, null)
                    flakes.forEach { it.draw(c) }
                }
            } finally {
                canvas?.let { surfaceHolder.unlockCanvasAndPost(it) }
            }
            handler.removeCallbacks(runnable)
            if (isVisible) handler.postDelayed(runnable, UPDATE_INTERVAL)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            log("onSurfaceDestroyed")
            handler.removeCallbacks(runnable)
            sensorManager.unregisterListener(sensorEventListener)
            super.onSurfaceDestroyed(holder)
        }
    }

    companion object {
        private const val TAG = "WallpaperServiceImpl"
        private const val FLAKES_COUNT = 200
        private const val UPDATE_INTERVAL = 5L
        private const val MAX_ANGLE = 45f
        var width = 0
        var height = 0
        var roll = 0f // y axis
        var pitch = 0f // z axis

        private fun log(logMessage: String) {
            Log.d(TAG, logMessage)
        }
    }
}
