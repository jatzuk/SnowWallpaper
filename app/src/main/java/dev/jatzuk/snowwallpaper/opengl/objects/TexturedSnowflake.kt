package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.Matrix.*
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.data.RectangleVertexArray
import dev.jatzuk.snowwallpaper.opengl.programs.SnowflakeProgram
import dev.jatzuk.snowwallpaper.opengl.util.BYTES_PER_FLOAT
import dev.jatzuk.snowwallpaper.opengl.util.loadTextureForOpenGL
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService.Companion.height
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService.Companion.width
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

class TexturedSnowflake(context: Context) {

    private val snowflakeProgram = SnowflakeProgram(context)
    private val snowflakeLimit = PreferenceRepository.getInstance(context).getSnowflakeLimit()
    private val snowflakes = Array(snowflakeLimit) { Snowflake(context, true) }
    private val snowflakeVertexArray = RectangleVertexArray(snowflakes, TOTAL_COMPONENT_COUNT)
    private val textureId = loadTextureForOpenGL(context, ImageProvider.ImageType.SNOWFLAKE_TEXTURE)

    private fun bindData() {
        snowflakeVertexArray.apply {
            updateBuffer()
            setVertexAttribPointer(
                0,
                snowflakeProgram.aPositionLocation,
                POSITION_COMPONENT_COUNT,
                STRIDE,
                true
            )

            setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                snowflakeProgram.aTextureLocation,
                TEXTURE_COMPONENT_COUNT,
                STRIDE
            )
        }
    }

    private fun unbindData() {
        glDisableVertexAttribArray(snowflakeProgram.aPositionLocation)
    }

    fun draw(mvpMatrix: FloatArray, modelMatrix: FloatArray, viewProjectionMatrix: FloatArray) {
        snowflakeProgram.useProgram()

        bindData()
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        snowflakes.forEachIndexed { index, snowflake ->
            setIdentityM(modelMatrix, 0)

            if (snowflake.shouldRotate) {
                val x = snowflake.x * 2f / width - 1f
                val y = snowflake.y * -2f / height + 1f
                rotate(modelMatrix, x, y, snowflake.rotationAxis, snowflake.rotationDegrees)
                multiplyMM(mvpMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
            }

            snowflakeProgram.setUniforms(mvpMatrix, textureId)

            snowflake.fall()
            glDrawArrays(GL_TRIANGLE_STRIP, index * 4, 4)
        }

        unbindData()
        glDisable(GL_BLEND)
    }

    private fun rotate(
        modelMatrix: FloatArray,
        x: Float,
        y: Float,
        rotationAxis: Snowflake.RotationAxis,
        angle: Float
    ) {
        translateM(modelMatrix, 0, x, y, 1f)
        rotateAxis(modelMatrix, angle, rotationAxis)
        translateM(modelMatrix, 0, -x, -y, -1f)
    }

    private fun rotateAxis(
        modelMatrix: FloatArray,
        angle: Float,
        rotationAxis: Snowflake.RotationAxis
    ) {
        when (rotationAxis) {
            Snowflake.RotationAxis.X -> rotateM(modelMatrix, 0, angle, 1f, 0f, 0f)
            Snowflake.RotationAxis.Y -> rotateM(modelMatrix, 0, angle, 0f, 1f, 0f)
            Snowflake.RotationAxis.Z -> rotateM(modelMatrix, 0, angle, 0f, 0f, 1f)
            else -> Unit
        }
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
        private const val TEXTURE_COMPONENT_COUNT = 2
        private const val TOTAL_COMPONENT_COUNT = (POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT) +
                (TEXTURE_COMPONENT_COUNT * BYTES_PER_FLOAT)
        private const val STRIDE =
            (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }
}
