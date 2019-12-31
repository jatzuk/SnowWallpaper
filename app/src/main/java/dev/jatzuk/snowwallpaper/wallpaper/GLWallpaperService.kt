package dev.jatzuk.snowwallpaper.wallpaper
//
//import android.content.Context
//import android.graphics.*
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import android.os.Handler
//import android.os.SystemClock
//import android.service.wallpaper.WallpaperService
//import android.util.Log
//import android.view.SurfaceHolder
//import dev.jatzuk.snowwallpaper.R
//import dev.jatzuk.snowwallpaper.Snowflake
//import dev.jatzuk.snowwallpaper.util.BitmapManager
//import kotlin.math.PI
//import kotlin.math.abs
//import kotlin.math.atan2
//
//class GLWallpaperService : WallpaperService() {
//    override fun onCreateEngine(): Engine = WallpaperServiceEngineImpl()
//
//    private inner class WallpaperServiceEngineImpl : Engine() {
//        private val handler = Handler()
//        private val runnable: Runnable = Runnable { update() }
//        private lateinit var sensorManager: SensorManager
//        private var snowflakeResourceId = 0
//
//        // todo(debug)
//        private var frameCounter = 0
//        private var lastFrameUpdateTimestamp = 0L
//        private var fps = 0
//        private var framesSkipped = 0
//        private var averageFrameUpdateTime = arrayListOf<Long>()
//        private var averageFPS = arrayListOf<Int>()
//
//        private val sensorEventListener = object : SensorEventListener {
//            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//            }
//
//            override fun onSensorChanged(event: SensorEvent) {
//                val x = event.values[0]
//                val y = event.values[1]
//                val azimuth = atan2(x, y) / (PI / 180)
//                if (azimuth in -MAX_ANGLE..MAX_ANGLE) roll = azimuth.toFloat()
//                val positiveY = abs(y)
//                if (positiveY > 1.2f) pitch = positiveY
//            }
//        }
//
//        override fun onCreate(surfaceHolder: SurfaceHolder?) {
//            BitmapManager.generateBitmaps(applicationContext,
//                R.drawable.snowflake
//            )
//            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//            snowflakeResourceId =
//                R.drawable.snowflake // todo(setup choice)
//        }
//
//        override fun onSurfaceChanged(
//            holder: SurfaceHolder?,
//            format: Int,
//            width: Int,
//            height: Int
//        ) {
//            log("onSurfaceChanged")
//            Companion.width = width
//            Companion.height = height
//            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//            sensorManager.registerListener(
//                sensorEventListener,
//                accelerometer,
//                SensorManager.SENSOR_DELAY_NORMAL
//            )
//            flakes = Array(
//                FLAKES_COUNT
//            ) {
//                Snowflake(/*false*/it < FLAKES_COUNT - FLAKES_COUNT_BIG)
//            }
//            super.onSurfaceChanged(holder, format, width, height)
//        }
//
//        override fun onVisibilityChanged(visible: Boolean) {
//            log("onVisibilityChanged")
//            if (visible) {
//                log(
//                    "onVisibilityChanged - VISIBLE"
//                )
//                handler.post(runnable)
//            } else handler.removeCallbacks(runnable)
//            super.onVisibilityChanged(visible)
//        }
//
//        private fun measureRenderStatistics() {
//            frameCounter++
//            val now = SystemClock.uptimeMillis()
//            val delta = now - lastFrameUpdateTimestamp
//            if (delta > 1000L) {
//                fps = frameCounter * 1000 / delta.toInt()
//                frameCounter = 0
//                lastFrameUpdateTimestamp = now
//                averageFPS.add(fps)
//            }
////            averageFrameUpdateTime.add()
//        }
//
//        private fun update() {
//            var canvas: Canvas? = null
//            try {
//                canvas = surfaceHolder?.lockCanvas()
//                canvas?.let { c ->
//                    c.drawColor(Color.BLACK)
////                      c.drawBitmap(bitmap, 0f, 0f, null) // todo("scale to device props")
//                    for (snowflake in flakes) snowflake.render(c)
//
//                    measureRenderStatistics()
////                        displayDebugInfo("HA : ${c.isHardwareAccelerated}", c, 0f, height.toFloat() - 100)
//                    displayDebugInfo(
//                        "FPS: $fps",
//                        c,
//                        0f,
//                        height.toFloat() - 50
//                    )
//                }
//            } finally {
//                canvas?.let { surfaceHolder.unlockCanvasAndPost(it) }
//            }
//            handler.removeCallbacks(runnable)
//            if (isVisible) handler.postDelayed(runnable,
//                UPDATE_INTERVAL
//            )
//        }
//
//        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
//            log("onSurfaceDestroyed")
//            handler.removeCallbacks(runnable)
//            sensorManager.unregisterListener(sensorEventListener)
//            log("average render time: ${averageFrameUpdateTime.average()}")
//            log("average FPS: ${averageFPS.average()}")
//            super.onSurfaceDestroyed(holder)
//        }
//    }
//
//    companion object {
//        private const val TAG = "WallpaperServiceImpl"
//        private const val UPDATE_INTERVAL = 5L
//        private const val MAX_ANGLE = 45f
//        lateinit var flakes: Array<Snowflake>
//        const val FLAKES_COUNT = 150
//        const val FLAKES_COUNT_BIG = 5
//        var width = 0
//        var height = 0
//        var roll = 0f // x axis (azimuth coefficient)
//        var pitch = 0f // y axis
//
//        private val debugPaint = Paint().apply {
//            color = Color.WHITE
//            textSize = 25f
//        }
//
//        fun log(logMessage: String) {
//            Log.d(TAG, logMessage)
//        }
//
//        fun displayDebugInfo(text: String, canvas: Canvas, x: Float = 100f, y: Float = 200f) {
//            val bounds = Rect()
//            debugPaint.getTextBounds(text, 0, text.length, bounds)
//            canvas.drawText(text, x, y,
//                debugPaint
//            )
//        }
//
//    }
//}
