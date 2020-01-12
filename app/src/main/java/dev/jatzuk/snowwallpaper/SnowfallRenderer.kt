package dev.jatzuk.snowwallpaper

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import android.os.SystemClock
import androidx.preference.PreferenceManager
import dev.jatzuk.snowwallpaper.objects.SnowfallBackground
import dev.jatzuk.snowwallpaper.programs.SnowfallProgram
import dev.jatzuk.snowwallpaper.util.Logger.logging
import dev.jatzuk.snowwallpaper.util.loadTexture
import dev.jatzuk.snowwallpaper.views.MainActivity.Companion.ratio
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SnowfallRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private lateinit var snowfallProgram: SnowfallProgram
    private lateinit var snowfallBackground: SnowfallBackground
    private var textureId = 0
    private var frameStartMs = 0L

    private var startTimeMs = 0L
    private var frames = 0

    private val isSnowfallBackgroundProgramUsed =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.background_snowflakes_global_switcher_key), true)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 0f)

        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)

//        glEnable(GL_DEPTH_TEST)

        snowfallProgram = SnowfallProgram(context)
        textureId = loadTexture(context, R.drawable.background_snowflake_texture)

        snowfallProgram.useProgram()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        ratio = width.toFloat() / height.toFloat()

        if (isSnowfallBackgroundProgramUsed) {
            // we need to initialize background after getting right aspect ratio from above
            snowfallBackground = SnowfallBackground(snowfallProgram, context)
        } else {
            logging("snowfall program is not using", TAG)
        }

        frustumM(projectionMatrix, 0, ratio, -ratio, -1f, 1f, 3f, 7f)
        setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        limitFrameRate(FRAME_LIMIT)
        logFrameRate()

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        if (isSnowfallBackgroundProgramUsed) {
            snowfallProgram.setUniforms(viewProjectionMatrix, Color.WHITE, textureId)
            snowfallBackground.run {
                bindData()
                draw()
            }
        }
    }

    private fun limitFrameRate(@Suppress("SameParameterValue") framesPerSecond: Int) {
        val elapsedMs = SystemClock.elapsedRealtime() - frameStartMs
        val expectedMs = 1000 / framesPerSecond
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
        private val TAG = SnowfallRenderer::class.java.simpleName
        private const val FRAME_LIMIT = 30 //todo(load from prefs)
    }
}
