package dev.jatzuk.snowwallpaper.opengl

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import android.os.SystemClock
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.objects.SnowfallBackground
import dev.jatzuk.snowwallpaper.opengl.objects.Triangle
import dev.jatzuk.snowwallpaper.utilities.Logger.logging
import dev.jatzuk.snowwallpaper.ui.MainActivity.Companion.ratio
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SnowfallRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val preferenceRepository = PreferenceRepository.getInstance(context)

    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private lateinit var snowfallBackground: SnowfallBackground
    private var frameStartMs = 0L

    private var frameLimit = preferenceRepository.getRendererFrameLimit()
    private var startTimeMs = 0L
    private var frames = 0

    private val isSnowfallBackgroundProgramUsed = preferenceRepository.getIsSnowfallEnabled()

    private lateinit var triangle: Triangle

    private val triangleMVPMatrix = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 0f)

        glBlendFunc(GL_ONE, GL_ONE)

        triangle = Triangle(context)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        ratio = width.toFloat() / height.toFloat()

        if (isSnowfallBackgroundProgramUsed) {
//         we need to initialize background after getting right aspect ratio from above
            snowfallBackground = SnowfallBackground(context)
        } else {
            logging("snowfall program is not using", TAG)
        }

        frustumM(projectionMatrix, 0, ratio, -ratio, -1f, 1f, 3f, 7f)
        setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        limitFrameRate()
        logFrameRate()

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        triangle.draw(triangleMVPMatrix)
        updateMatrices()

        if (isSnowfallBackgroundProgramUsed) {
//            multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            snowfallBackground.draw(viewProjectionMatrix)
        }
    }

    private fun updateMatrices() {
//        update after rotations
        multiplyMM(viewProjectionMatrix, 0, viewMatrix, 0, triangleMVPMatrix, 0)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewProjectionMatrix, 0)
    }

    private fun limitFrameRate() {
        val elapsedMs = SystemClock.elapsedRealtime() - frameStartMs
        val expectedMs = 1000 / frameLimit
        val sleepTime = expectedMs - elapsedMs

        if (sleepTime > 0) SystemClock.sleep(sleepTime)
        frameStartMs = SystemClock.elapsedRealtime()
    }

    private fun logFrameRate() {
        val elapsedMs = SystemClock.elapsedRealtime()
        val elapsedSec = (elapsedMs - startTimeMs) / 1000

        if (elapsedSec >= 1) {
            logging("FPS: ${frames / elapsedSec}", TAG)
            startTimeMs = SystemClock.elapsedRealtime()
            frames = 0
        }

        frames++
    }

    companion object {
        private const val TAG = "SnowfallRenderer"
    }
}
