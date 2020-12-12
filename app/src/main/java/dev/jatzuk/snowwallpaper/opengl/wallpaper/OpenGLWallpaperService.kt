package dev.jatzuk.snowwallpaper.opengl.wallpaper

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.os.Build
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

class OpenGLWallpaperService : WallpaperService() {

    private var sensorManager: SensorManager? = null
    private var engine: WallpaperEngine? = null

    override fun onCreateEngine(): Engine = WallpaperEngine().also { engine = it }

    override fun onLowMemory() {
        Logger.d("onLowMemory", TAG)
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        Logger.d("onTrimMemory", TAG)
        engine?.unregisterSensorListener()
        engine = null
        super.onTrimMemory(level)
    }

    override fun onCreate() {
        Logger.d("onCreate", TAG)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.d("onStartCommand $intent $flags $startId", TAG)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Logger.d("onDestroy", TAG)
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Logger.d("onUnbind $intent", TAG)
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        Logger.d("onRebind $intent", TAG)
        super.onRebind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Logger.d("onTaskRemoved", TAG)
        super.onTaskRemoved(rootIntent)
    }

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

            Logger.d("Wallpaper Engine firebaseAnalytics initialized", WALLPAPER_ENGINE_TAG)
            Logger.d("Wallpaper Engine onCreate()", WALLPAPER_ENGINE_TAG)

            preferenceRepository = PreferenceRepository.getInstance(this@OpenGLWallpaperService)
            display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay

            if (isRollSensorEnabled || isPitchSensorEnabled) {
                Logger.d("Wallpaper Engine createSensorListener()", WALLPAPER_ENGINE_TAG)
                sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            }

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
            Logger.d("Wallpaper Engine onVisibilityChanged(): $visible", TAG)
            Logger.d("Wallpaper Engine isRendererSet: $isRendererSet", TAG)

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

        override fun onSensorChanged(event: SensorEvent) {
            logOnSensorChanged()
            Logger.d(
                "Sensor values: ${event.values?.contentToString()}",
                SENSOR_INFO_TAG,
                sendToFBA = false
            )
            val rollAdjustment = when (display?.rotation) {
                Surface.ROTATION_90 -> -event.values[1]
                Surface.ROTATION_270 -> event.values[1]
                else -> event.values[0]
            }
            roll = if (isRollSensorEnabled) rollAdjustment * rollSensorSensitivity else 0f
            pitch = if (isPitchSensorEnabled) -event.values[2] * pitchSensorSensitivity else 0f
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            val accuracyName = when (accuracy) {
                SensorManager.SENSOR_STATUS_NO_CONTACT -> "SENSOR_STATUS_NO_CONTACT"
                SensorManager.SENSOR_STATUS_UNRELIABLE -> "SENSOR_STATUS_UNRELIABLE"
                SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "SENSOR_STATUS_ACCURACY_LOW"
                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "SENSOR_STATUS_ACCURACY_MEDIUM"
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "SENSOR_STATUS_ACCURACY_HIGH"
                else -> "SENSOR_UNKNOWN_STATUS"
            }
            Logger.d("Sensor onAccuracyChanged() $sensor | $accuracyName", TAG)
        }

        private fun registerSensorListener() {
            Logger.d("Wallpaper Engine registerSensorListener()", SENSOR_INFO_TAG)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                sensorManager?.registerListener(
                    this,
                    sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_UI,
                    2000
                )
            } else {
                sensorManager?.registerListener(
                    this,
                    sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        }

        fun unregisterSensorListener() {
            Logger.d("Wallpaper Engine unregisterSensorListener()", TAG)
            sensorManager?.unregisterListener(this)
        }

        override fun onDestroy() {
            super.onDestroy()
            Logger.d("Wallpaper Engine onDestroy() | inPreview: $isPreview", TAG)
            glSurfaceView.onWallpaperDestroy()
            display = null
            isRendererSet = false
            unregisterSensorListener()
            TextureCache.getInstance().clear()
        }

        private fun updateSensorSensitivityValues() {
            Logger.d("Wallpaper Engine updateSensorSensitivityValues()", SENSOR_INFO_TAG)
            isRollSensorEnabled = preferenceRepository.getIsRollSensorEnabled()
            isPitchSensorEnabled = preferenceRepository.getIsPitchSensorEnabled()

            rollSensorSensitivity = preferenceRepository.getRollSensorSensitivity() * ROLL_RATIO
            pitchSensorSensitivity = preferenceRepository.getPitchSensorSensitivity() * PITH_RATIO
        }

        private fun logOnSensorChanged() {
            val elapsedMs = SystemClock.elapsedRealtime()
            val elapsedSec = (elapsedMs - lastLogTime) / 1000

            if (elapsedSec >= sensorLogInterval / 1000) {
                Logger.d("Wallpaper Engine onSensorChanged()", SENSOR_INFO_TAG)
                lastLogTime = SystemClock.elapsedRealtime()
            }
        }

        private inner class WallpaperGLSurfaceView(context: Context) : GLSurfaceView(context) {

            override fun getHolder(): SurfaceHolder = surfaceHolder

            fun onWallpaperDestroy() {
                super.onDetachedFromWindow()
                Logger.d(
                    "${this::class.java.simpleName} onWallpaperDestroy()",
                    "WallpaperGLSurfaceView"
                )
            }
        }
    }

    companion object {

        private const val SENSOR_INFO_TAG = "SENSOR_INFO_TAG"
        private const val TAG = "OpenGLWallpaperService"
        private const val WALLPAPER_ENGINE_TAG = "WALLPAPER_ENGINE_TAG"
        private const val ROLL_RATIO = 0.0001f
        private const val PITH_RATIO = 0.01f
        var ratio = 0f
        var roll = 0f
        var pitch = 0f
        var width = 0
        var height = 0
    }
}
