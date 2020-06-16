package dev.jatzuk.snowwallpaper.opengl

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import android.os.SystemClock
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.objects.BackgroundImage
import dev.jatzuk.snowwallpaper.opengl.objects.OpenGLSceneObject
import dev.jatzuk.snowwallpaper.opengl.objects.Snowfall
import dev.jatzuk.snowwallpaper.opengl.objects.TexturedSnowfall
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService.Companion.ratio
import dev.jatzuk.snowwallpaper.utilities.Logger.errorLog
import dev.jatzuk.snowwallpaper.utilities.Logger.logging
import java.lang.ref.WeakReference
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SnowfallRenderer(context: Context) : GLSurfaceView.Renderer {

    private val contextReference = WeakReference(context)
    private val preferenceRepository = PreferenceRepository.getInstance(context)

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private val openGLSceneObjectsHolder = OpenGLSceneObjectHolder()

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
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        orthoM(projectionMatrix, 0, -1f, 1f, -1f, 1f, 1f, 10f)

        ratio = if (width > height) width.toFloat() / height else height.toFloat() / width
        OpenGLWallpaperService.width = width
        OpenGLWallpaperService.height = height

        openGLSceneObjectsHolder.run {
            populateActiveOpenGLSceneObjects()
            openGLSceneObjects.forEach { it?.updateOpenGLValues(contextReference.get()!!) }
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        logFrameRate()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        openGLSceneObjectsHolder.openGLSceneObjects.forEach { it?.draw() }
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

    private inner class OpenGLSceneObjectHolder(sceneObjectsCount: Int = 3) {

        private val context = contextReference.get()!!
        val openGLSceneObjects = arrayOfNulls<OpenGLSceneObject>(sceneObjectsCount)

        fun populateActiveOpenGLSceneObjects() {
            openGLSceneObjects[0] = if (preferenceRepository.getIsBackgroundImageEnabled()) {
                getOpenGLSceneObjectByTypeTag(BackgroundImage.TAG)
            } else null

            var usageMessage = if (openGLSceneObjects[0] == null) IS_NOT_USING else IS_USING
            logging("${BackgroundImage.TAG} program $usageMessage", OPEN_GL_SCENE_RESOLVER_TAG)

            openGLSceneObjects[1] = if (preferenceRepository.getIsSnowfallEnabled()) {
                getOpenGLSceneObjectByTypeTag(Snowfall.TAG)
            } else null

            usageMessage = if (openGLSceneObjects[1] == null) IS_NOT_USING else IS_USING
            logging("${Snowfall.TAG} program $usageMessage", OPEN_GL_SCENE_RESOLVER_TAG)

            openGLSceneObjects[2] = if (preferenceRepository.getIsSnowflakeEnabled()) {
                getOpenGLSceneObjectByTypeTag(TexturedSnowfall.TAG)
            } else null

            usageMessage = if (openGLSceneObjects[2] == null) IS_NOT_USING else IS_USING
            logging("${TexturedSnowfall.TAG} program $usageMessage", OPEN_GL_SCENE_RESOLVER_TAG)
        }

        private fun getOpenGLSceneObjectByTypeTag(type: String): OpenGLSceneObject = when (type) {
            BackgroundImage.TAG -> {
                if (openGLSceneObjects[0] == null)
                    BackgroundImage(context, mvpMatrix, modelMatrix, viewProjectionMatrix)
                else openGLSceneObjects[0]!!
            }
            Snowfall.TAG -> {
                if (openGLSceneObjects[1] == null)
                    Snowfall(context, mvpMatrix, modelMatrix, viewProjectionMatrix)
                else openGLSceneObjects[1]!!
            }
            TexturedSnowfall.TAG -> {
                if (openGLSceneObjects[2] == null)
                    TexturedSnowfall(context, mvpMatrix, modelMatrix, viewProjectionMatrix)
                else openGLSceneObjects[2]!!
            }
            else -> {
                val message = "Illegal OpenGLSceneObject with type: $type"
                val e = IllegalArgumentException(message)
                errorLog(message, OPEN_GL_SCENE_RESOLVER_TAG, e)
                throw e
            }
        }
    }

    companion object {
        private const val TAG = "SnowfallRenderer"
        private const val OPEN_GL_SCENE_RESOLVER_TAG = "OpenGLSceneResolver"
        private const val IS_NOT_USING = "is not used"
        private const val IS_USING = "is used"
    }
}
