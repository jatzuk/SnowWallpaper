package dev.jatzuk.snowwallpaper.opengl.wallpaper

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.view.Display
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.data.preferences.TextureCache
import dev.jatzuk.snowwallpaper.opengl.SnowfallRenderer
import dev.jatzuk.snowwallpaper.utilities.Logger
import dev.jatzuk.snowwallpaper.utilities.Logger.logging

class OpenGLWallpaperService : WallpaperService() {

    private lateinit var sensorManager: SensorManager

    override fun onCreateEngine(): Engine = WallpaperEngine()

    private inner class WallpaperEngine : Engine(), SensorEventListener {

        private lateinit var preferenceRepository: PreferenceRepository
        private var isRollSensorEnabled = true
        private var isPitchSensorEnabled = true
        private var rollSensorSensitivity = 0f
        private var pitchSensorSensitivity = 0f
        private lateinit var glSurfaceView: WallpaperGLSurfaceView
        private lateinit var renderer: SnowfallRenderer
        private var isRendererSet = false
        private var display: Display? = null
        private var lastLogTime = 0L
        private var sensorLogInterval = 10_000

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

            Logger.initFirebaseAnalytics(baseContext)

            logging("$wallpaperEngineClassName firebaseAnalytics initialized", TAG)
            logging("$wallpaperEngineClassName onCreate()", TAG)

            preferenceRepository = PreferenceRepository.getInstance(baseContext)
            display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay

            updateSensorSensitivityValues()

            if (isRollSensorEnabled || isPitchSensorEnabled) {
                logging("$wallpaperEngineClassName createSensorListener()", SENSOR_INFO_TAG)
                sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            }

            glSurfaceView = WallpaperGLSurfaceView(baseContext)
            renderer = SnowfallRenderer(baseContext)

            glSurfaceView.run {
                setEGLContextClientVersion(2)
                preserveEGLContextOnPause = true
                setRenderer(renderer)
                isRendererSet = true
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            logging("$wallpaperEngineClassName onVisibilityChanged(): $visible", TAG)
            logging("$wallpaperEngineClassName isRendererSet: $isRendererSet", TAG)

            if (isRendererSet) {
                if (visible) {
                    updateSensorSensitivityValues()
                    registerSensorListener()
                    if (!isPreview) TextureCache.getInstance().clear()
                    glSurfaceView.onResume()
                } else {
                    unregisterSensorListener()
                    glSurfaceView.onPause()
                }
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            logging("$wallpaperEngineClassName onDestroy()", TAG)
            glSurfaceView.onWallpaperDestroy()
            display = null
            isRendererSet = false
            unregisterSensorListener()
//            TextureCache.getInstance().clear()
//            logging("inPreview: $isPreview", "PREVIEW")
//            if (isPreview) TextureCache.getInstance().clear()
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            logOnSensorChanged()
            event?.let {
                logging(
                    "sensor values: ${it.values?.contentToString()}",
                    SENSOR_INFO_TAG,
                    translateToFirebase = false
                )
                val rollAdjustment = when (display?.rotation) {
                    Surface.ROTATION_90 -> -it.values[1]
                    Surface.ROTATION_270 -> it.values[1]
                    else -> it.values[0]
                }
                roll =
                    if (isRollSensorEnabled) rollAdjustment * rollSensorSensitivity else 0f
                pitch =
                    if (isPitchSensorEnabled) -it.values[2] * pitchSensorSensitivity else 0f
            }
        }

        private fun updateSensorSensitivityValues() {
            logging("$wallpaperEngineClassName updateSensorSensitivityValues()", SENSOR_INFO_TAG)
            isRollSensorEnabled = preferenceRepository.getIsRollSensorEnabled()
            isPitchSensorEnabled = preferenceRepository.getIsPitchSensorEnabled()

            rollSensorSensitivity = preferenceRepository.getRollSensorSensitivity() * ROLL_RATIO
            pitchSensorSensitivity = preferenceRepository.getPitchSensorSensitivity() * PITH_RATIO
        }

        private fun registerSensorListener() {
            logging("$wallpaperEngineClassName registerSensorListener()", SENSOR_INFO_TAG)
            sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        private fun unregisterSensorListener() {
            logging("$wallpaperEngineClassName unregisterSensorListener()", SENSOR_INFO_TAG)
            sensorManager.unregisterListener(this)
        }

        private fun logOnSensorChanged() {
            val elapsedMs = SystemClock.elapsedRealtime()
            val elapsedSec = (elapsedMs - lastLogTime) / 1000

            if (elapsedSec >= sensorLogInterval / 1000) {
                logging("$wallpaperEngineClassName onSensorChanged()", SENSOR_INFO_TAG)
                lastLogTime = SystemClock.elapsedRealtime()
            }
        }

        private inner class WallpaperGLSurfaceView(context: Context) : GLSurfaceView(context) {

            override fun getHolder(): SurfaceHolder = surfaceHolder

            fun onWallpaperDestroy() {
                super.onDetachedFromWindow()
                logging(
                    "${this::class.java.simpleName} onWallpaperDestroy()",
                    "WallpaperGLSurfaceView"
                )
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

        private val wallpaperEngineClassName = WallpaperEngine::class.java.simpleName
    }
}
