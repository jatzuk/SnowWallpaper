package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.Matrix.*
import android.os.SystemClock
import dev.jatzuk.snowwallpaper.opengl.data.CubeArray
import dev.jatzuk.snowwallpaper.opengl.programs.SnowflakeProgram

class Triangle(context: Context) {

    private val snowflakeProgram = SnowflakeProgram(context)
    private val vertices = floatArrayOf(
//        -0.4f, -0.4f, 0.0f,
//        0.4f, 0f, 0f, 1f,

//        0.4f, -0.4f, 0.0f,
//        0f, 0.4f, 0f, 1f,

//        0.0f, 0.3f, 0.0f,
//        0f, 0f, 1f, 1f

        -1f, -1f, 0f,
        0f, 0f, 1f, 1f,

        1f, -1f, 0f,
        0f, 1f, 0f, 1f,

        0f, 1f, 0f,
        0f, 0f, 1f, 1f

//        -0.5f, -0.5f, 0f,
//        -0.5f, 0.7f, 0f,
//        0f, 0.7f, 0f

//        0f, 0.7f, 0f,
//        0.5f, 0.7f, 0f,
//        0.5f, -0.5f, 0f
    )
    private val vertexArray = CubeArray(vertices)

    private fun bindData() {
        vertexArray.setVertexAttribPointer(
            0,
            snowflakeProgram.aPositionLocation,
            POSITION_COMPONENT_COUNT,
            STRIDE
        )

        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            snowflakeProgram.aColorLocation,
            COLOR_COMPONENT_COUNT,
            STRIDE
        )
    }

    private fun unbindData() {
        glDisableVertexAttribArray(snowflakeProgram.aPositionLocation)
    }

    fun draw(mvpMatrix: FloatArray) {
        rotate(mvpMatrix)

        bindData()
        snowflakeProgram.run {
            useProgram()
            setUniforms(mvpMatrix)
        }

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_LEQUAL)

        glDrawArrays(GL_TRIANGLES, 0, 1 * POSITION_COMPONENT_COUNT)

        glDisable(GL_DEPTH_TEST)
        glDisable(GL_LEQUAL)
        unbindData()
    }

    private fun rotate(mvpMatrix: FloatArray) {
        val time = (SystemClock.uptimeMillis() % 10_000L).toInt()
        val angleInDegrees = (360f / 10_000f) * time
        setIdentityM(mvpMatrix, 0)
//        translateM(mvpMatrix, 0, 0f, 0f, 0f)
        rotateM(mvpMatrix, 0, angleInDegrees, 0f, 1f, 0f)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
        private const val COLOR_COMPONENT_COUNT = 4
        private const val STRIDE = 0
    }
}
