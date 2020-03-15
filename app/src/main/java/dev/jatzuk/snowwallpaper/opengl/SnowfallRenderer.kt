package dev.jatzuk.snowwallpaper.opengl

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import android.os.SystemClock
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.objects.BackgroundImage
import dev.jatzuk.snowwallpaper.opengl.objects.SnowfallBackground
import dev.jatzuk.snowwallpaper.opengl.objects.TexturedSnowflake
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

    private var snowfallBackground: SnowfallBackground? = null
    private var texturedSnowflake: TexturedSnowflake? = null
    private var backgroundImage: BackgroundImage? = null

    private val preferenceRepository = PreferenceRepository.getInstance(context)

    private var frameStartMs = 0L
    private var frameLimit = 0
    private var startTimeMs = 0L

    private var frames = 0

    private var isSnowfallBackgroundProgramUsed = false
    private var isSnowflakeProgramUsed = false
    private var isBackgroundImageUsed = false

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

        ratio = if (width > height) width.toFloat() / height else height.toFloat() / width

        OpenGLWallpaperService.width = width
        OpenGLWallpaperService.height = height

        frameLimit = preferenceRepository.getRendererFrameLimit() // todo fix 60fps x2 speed

        isSnowfallBackgroundProgramUsed = preferenceRepository.getIsSnowfallEnabled()
        isSnowflakeProgramUsed = preferenceRepository.getIsSnowflakeEnabled()
        isBackgroundImageUsed = preferenceRepository.getIsBackgroundImageEnabled()

        snowfallBackground =
            if (isSnowfallBackgroundProgramUsed) SnowfallBackground(context) else null

        var usageMessage = if (snowfallBackground != null) IS_USING else IS_NOT_USING
        logging("snowfall program $usageMessage", TAG)

        texturedSnowflake = if (isSnowflakeProgramUsed) TexturedSnowflake(context) else null

        usageMessage = if (texturedSnowflake != null) IS_USING else IS_NOT_USING
        logging("snowflake program $usageMessage", TAG)

        backgroundImage = if (isBackgroundImageUsed) BackgroundImage(context) else null

        usageMessage = if (backgroundImage != null) IS_USING else IS_NOT_USING
        logging("background image $usageMessage", TAG)

        orthoM(projectionMatrix, 0, -1f, 1f, -1f, 1f, 1f, 10f)
    }

    override fun onDrawFrame(gl: GL10?) {
        limitFrameRate()
        logFrameRate()

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        backgroundImage?.draw(mvpMatrix, modelMatrix, viewProjectionMatrix)
        snowfallBackground?.draw(mvpMatrix, modelMatrix, viewProjectionMatrix)
        texturedSnowflake?.draw(mvpMatrix, modelMatrix, viewProjectionMatrix)
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
        private const val IS_NOT_USING = "is not used"
        private const val IS_USING = "is used"
    }
}
