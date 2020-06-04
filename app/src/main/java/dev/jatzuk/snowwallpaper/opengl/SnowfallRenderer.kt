package dev.jatzuk.snowwallpaper.opengl

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import android.os.SystemClock
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.objects.BackgroundImage
import dev.jatzuk.snowwallpaper.opengl.objects.OpenGLRenderingObject
import dev.jatzuk.snowwallpaper.opengl.objects.Snowfall
import dev.jatzuk.snowwallpaper.opengl.objects.TexturedSnowfall
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService.Companion.ratio
import dev.jatzuk.snowwallpaper.utilities.Logger.logging
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SnowfallRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private val renderingEntity = arrayOfNulls<OpenGLRenderingObject>(3)

    private var frameStartMs = 0L
    private var frameLimit = 0
    private var startTimeMs = 0L

    private var frames = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 0f)
        glBlendFunc(GL_ONE, GL_ONE)

        val eyeX = 0f
        val eyeY = 0f
        val eyeZ = 3f

        val centerX = 0f
        val centerY = 0f
        val centerZ = 0f

        val upX = 0f
        val upY = 1f
        val upZ = 0f

        setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)

        renderingEntity[0] = Snowfall(context, mvpMatrix, modelMatrix, viewProjectionMatrix)
        renderingEntity[1] = TexturedSnowfall(context, mvpMatrix, modelMatrix, viewProjectionMatrix)
        renderingEntity[2] = BackgroundImage(context, mvpMatrix, modelMatrix, viewProjectionMatrix)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        orthoM(projectionMatrix, 0, -1f, 1f, -1f, 1f, 1f, 10f)

        ratio = if (width > height) width.toFloat() / height else height.toFloat() / width
        OpenGLWallpaperService.width = width
        OpenGLWallpaperService.height = height

        // todo fix 60fps x2 speed
        frameLimit = PreferenceRepository.getInstance(context).getRendererFrameLimit()

        renderingEntity.forEach {
            it?.updateValues()
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        limitFrameRate()
        logFrameRate()

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        renderingEntity.forEach { it?.draw() }
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
            logging("FPS: ${frames / elapsedSec}", TAG, translateToFirebase = false)
            startTimeMs = SystemClock.elapsedRealtime()
            frames = 0
        }

        frames++
    }

    companion object {
        private const val TAG = "SnowfallRenderer"
    }
}
