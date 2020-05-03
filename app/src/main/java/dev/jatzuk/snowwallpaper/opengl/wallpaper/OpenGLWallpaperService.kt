package dev.jatzuk.snowwallpaper.opengl.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.core.graphics.drawable.toDrawable
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.SnowfallRenderer

class OpenGLWallpaperService : WallpaperService() {

    private lateinit var sensorManager: SensorManager
    private lateinit var sensorEventListener: SensorEventListener

    override fun onCreateEngine(): Engine = WallpaperEngine()

    private inner class WallpaperEngine : Engine() {

        private lateinit var preferenceRepository: PreferenceRepository
        private var isRollSensorEnabled = true
        private var isPitchSensorEnabled = true
        private var rollSensorSensitivity = 0f
        private var pitchSensorSensitivity = 0f
        private lateinit var glSurfaceView: WallpaperGLSurfaceView
        private lateinit var renderer: SnowfallRenderer
        private var isRendererSet = false

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

            preferenceRepository = PreferenceRepository.getInstance(applicationContext)

            updateSensorSensitivityValues()

            if (isRollSensorEnabled || isPitchSensorEnabled) createSensorListener()

            glSurfaceView = WallpaperGLSurfaceView(this@OpenGLWallpaperService)
            renderer = SnowfallRenderer(this@OpenGLWallpaperService)

            glSurfaceView.run {
                setEGLContextClientVersion(2)
                preserveEGLContextOnPause = true
                setRenderer(renderer)
                isRendererSet = true
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (isRendererSet) {
                if (visible) {
                    updateSensorSensitivityValues()
                    registerSensorListener()
                    glSurfaceView.onResume()
                } else {
                    unregisterSensorListener()
                    glSurfaceView.onPause()
                }
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            glSurfaceView.onWallpaperDestroy()
        }

        private fun createSensorListener() {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensorEventListener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        roll =
                            if (isRollSensorEnabled) it.values[0] * rollSensorSensitivity else 0f
                        pitch =
                            if (isPitchSensorEnabled) -it.values[2] * pitchSensorSensitivity else 0f
                    }
                }
            }
        }

        private fun updateSensorSensitivityValues() {
            isRollSensorEnabled = preferenceRepository.getIsRollSensorEnabled()
            isPitchSensorEnabled = preferenceRepository.getIsPitchSensorEnabled()

            rollSensorSensitivity = preferenceRepository.getRollSensorSensitivity() * ROLL_RATIO
            pitchSensorSensitivity = preferenceRepository.getPitchSensorSensitivity() * PITH_RATIO
        }

        private fun registerSensorListener() {
            sensorManager.registerListener(
                sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        private fun unregisterSensorListener() {
            sensorManager.unregisterListener(sensorEventListener)
        }

        @Deprecated("prob to remove") // todo
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
        private const val TAG = "OpenGLWallpaperService"
        private const val ROLL_RATIO = 0.0001f
        private const val PITH_RATIO = 0.01f
        var ratio = 0f
        var roll = 0f
        var pitch = 0f
        var width = 0
        var height = 0
    }
}
