package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.Matrix.*
import android.os.SystemClock
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.opengl.data.VertexArray
import dev.jatzuk.snowwallpaper.opengl.programs.SnowflakeProgram
import dev.jatzuk.snowwallpaper.opengl.util.loadTexture

class Triangle(context: Context) {

    private val snowflakeProgram = SnowflakeProgram(context)
    private val textureId = loadTexture(context, R.drawable.texture_snowflake)
//    private val snowflakes = Array(1) { Snowflake(context, true) }
//    private val snowflakeVertexArray = SnowflakeVertexArray(snowflakes)

    private val vertices = floatArrayOf(
        -0.3f, 0.3f, 1f, 0f, 0f,
        -0.3f, -0.3f, 1f, 0f, 1f,
        0.3f, 0.3f, 1f, 1f, 0f,
        0.3f, -0.3f, 1f, 1f, 1f
    )
    private val vertexArray = VertexArray(vertices)

    private fun bindData() {
        vertexArray.apply {
//            updateBuffer()
            setVertexAttribPointer(
                0,
                snowflakeProgram.aPositionLocation,
                POSITION_COMPONENT_COUNT,
                STRIDE
            )

            setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                snowflakeProgram.aTextureLocation,
                TEXTURE_COUNT,
                STRIDE
            )
        }
    }

    private fun unbindData() {
        glDisableVertexAttribArray(snowflakeProgram.aPositionLocation)
    }

    fun draw(mvpMatrix: FloatArray, modelMatrix: FloatArray, viewProjectionMatrix: FloatArray) {
        setIdentityM(modelMatrix, 0)
        rotate(modelMatrix)
        multiplyMM(mvpMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)

        snowflakeProgram.run {
            useProgram()
            setUniforms(mvpMatrix, textureId)
        }
        bindData()
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)

//        snowflakes.forEachIndexed { index, snowflake ->
////            snowflake.fall()
//            snowflakeProgram.setPointSize(snowflake.radius)
//            glDrawArrays(GL_POINTS, 0, index + 1)
//        }

        glDisable(GL_BLEND)
        unbindData()
    }

    private fun rotate(modelMatrix: FloatArray) {
        val time = (SystemClock.uptimeMillis() % 10_000L).toInt()
        val angleInDegrees = (360f / 10_000f) * time
        translateM(modelMatrix, 0, 0f, 0f, 1f)
        rotateM(modelMatrix, 0, angleInDegrees, 0f, 1f, 0f)
        translateM(modelMatrix, 0, 0f, 0f, -1f)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
        private const val TEXTURE_COUNT = 2
        private const val COLOR_COMPONENT_COUNT = 4
        private const val STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COUNT) * 4
    }
}
