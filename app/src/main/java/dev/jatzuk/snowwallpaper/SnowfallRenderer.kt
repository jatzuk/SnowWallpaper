package dev.jatzuk.snowwallpaper

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import dev.jatzuk.snowwallpaper.data.VertexArray
import dev.jatzuk.snowwallpaper.util.BYTES_PER_FLOAT
import dev.jatzuk.snowwallpaper.util.ShaderHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SnowfallRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private val r = floatArrayOf(1f, 0f, 0f)
    private val g = floatArrayOf(0f, 1f, 0f)
    private val b = floatArrayOf(0f, 0f, 1f)

    private val vertices = floatArrayOf(
//        // X, Y, Z
//        // R, G, B, A
//
        // FRONT
        -1f, -1f, 1f,  // 0. left-bottom-front
        r[0], r[1], r[2], 1f,
        1f, -1f, 1f,  // 1. right-bottom-front
        g[0], g[1], g[2], 1f,
        -1f, 1f, 1f,  // 2. left-top-front
        b[0], b[1], b[2], 1f,

        -1f, 1f, 1f,  // 2. left-top-front
        r[0], r[1], r[2], 1f,
        1f, -1f, 1f,  // 1. right-bottom-front
        g[0], g[1], g[2], 1f,
        1f, 1f, 1f,  // 3. right-top-front
        b[0], b[1], b[2], 1f,

        // BACK
        1f, -1f, -1f,  // 6. right-bottom-back
        r[0], r[1], r[2], 1f,
        1f, 1f, -1f,  // 7. right-top-back
        g[0], g[1], g[2], 1f,
        -1f, -1f, -1f,  // 4. left-bottom-back
        b[0], b[1], b[2], 1f,

        -1f, -1f, -1f,  // 4. left-bottom-back
        r[0], r[1], r[2], 1f,
        -1f, 1f, -1f,  // 5. left-top-back
        g[0], g[1], g[2], 1f,
        1f, 1f, -1f,  // 7. right-top-back
        b[0], b[1], b[2], 1f,

        // LEFT
        -1f, -1f, -1f,  // 4. left-bottom-back
        r[0], r[1], r[2], 1f,
        -1f, -1f, 1f,  // 0. left-bottom-front
        g[0], g[1], g[2], 1f,
        -1f, 1f, 1f,  // 2. left-top-front
        b[0], b[1], b[2], 1f,

        -1f, 1f, 1f,  // 2. left-top-front
        r[0], r[1], r[2], 1f,
        -1f, 1f, -1f,  // 5. left-top-back
        g[0], g[1], g[2], 1f,
        -1f, -1f, -1f,  // 4. left-bottom-back
        b[0], b[1], b[2], 1f,

        // RIGHT
        1f, -1f, 1f,  // 1. right-bottom-front
        r[0], r[1], r[2], 1f,
        1f, -1f, -1f,  // 6. right-bottom-back
        g[0], g[1], g[2], 1f,
        1f, 1f, -1f,  // 7. right-top-back
        b[0], b[1], b[2], 1f,

        1f, 1f, -1f,  // 7. right-top-back
        r[0], r[1], r[2], 1f,
        1f, 1f, 1f,  // 3. right-top-front
        g[0], g[1], g[2], 1f,
        1f, -1f, 1f,  // 1. right-bottom-front
        b[0], b[1], b[2], 1f,

        // TOP
        -1f, 1f, 1f,  // 2. left-top-front
        r[0], r[1], r[2], 1f,
        1f, 1f, 1f,  // 3. right-top-front
        g[0], g[1], g[2], 1f,
        1f, 1f, -1f,  // 7. right-top-back
        b[0], b[1], b[2], 1f,

        1f, 1f, -1f,  // 7. right-top-back
        r[0], r[1], r[2], 1f,
        -1f, 1f, -1f,  // 5. left-top-back
        g[0], g[1], g[2], 1f,
        -1f, 1f, 1f,  // 2. left-top-front
        b[0], b[1], b[2], 1f,

        // BOTTOM
        -1f, -1f, -1f,  // 4. left-bottom-back
        r[0], r[1], r[2], 1f,
        1f, -1f, -1f,  // 6. right-bottom-back
        g[0], g[1], g[2], 1f,
        1f, -1f, 1f,   // 1. right-bottom-front
        b[0], b[1], b[2], 1f,
        1f, -1f, 1f,   // 1. right-bottom-front

        r[0], r[1], r[2], 1f,
        -1f, -1f, 1f,  // 0. left-bottom-front
        g[0], g[1], g[2], 1f,
        -1f, -1f, -1f,  // 4. left-bottom-back
        r[0], r[1], r[2], 1f
    )


    private val triangle = VertexArray(vertices)
    private val uMVPMatrix = "u_MVPMatrix"
    private val aColor = "a_Color"
    private val aPosition = "a_Position"
    private var aColorLocation = 0
    private var aPositionLocation = 0
    private var uMVPMatrixLocation = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 0f)
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)

        val vertexShaderSource =
            ShaderHelper.readShaderCodeFromResource(context, R.raw.simple_vertex_shader)
        val fragmentShaderSource =
            ShaderHelper.readShaderCodeFromResource(context, R.raw.simple_fragment_shader)
        val program = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource)
        glUseProgram(program)

        uMVPMatrixLocation = glGetUniformLocation(program, uMVPMatrix)
        aPositionLocation = glGetAttribLocation(program, aPosition)
        aColorLocation = glGetAttribLocation(program, aColor)

        triangle.setVertexAttribPointer(0, aPositionLocation, POSITION_COMPONENT_COUNT, STRIDE)
        triangle.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            aColorLocation,
            COLOR_COMPONENT_COUNT,
            STRIDE
        )

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 4.5f, 0f, 0f, -5f, 0f, 1f, 0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()
        val left = -ratio
        val right = ratio
        val bottom = -1f
        val top = 1f
        val near = 1f
        val far = 10f

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        glFrontFace(GL_CW)

        val time = (SystemClock.uptimeMillis() % 10_000L).toInt()
        val angleInDegrees = (360f / 10_000f) * time

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0f, 1f, 0f)
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f)
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0f, 0f, 1f)

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0)
        glDrawArrays(GL_TRIANGLES, 0, 12 * 3)
    }

    fun handleTouchDrag(dx: Float, dy: Float) {

    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
        private const val COLOR_COMPONENT_COUNT = 4
        private const val STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }
}
