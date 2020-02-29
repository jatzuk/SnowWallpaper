package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20.*
import android.opengl.Matrix.multiplyMM
import android.opengl.Matrix.setIdentityM
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.data.SnowflakeVertexArray
import dev.jatzuk.snowwallpaper.opengl.programs.SnowfallProgram
import dev.jatzuk.snowwallpaper.opengl.util.loadTexture

class SnowfallBackground(context: Context) {

    private val snowfallProgram = SnowfallProgram(context)
    private val textureId = loadTexture(context, R.drawable.texture_snowfall)

    private val snowflakesLimit = PreferenceRepository.getInstance(context).getSnowfallLimit()
    private val snowflakes = Array(snowflakesLimit) { Snowflake(context) }
    private val snowflakesVertexArray = SnowflakeVertexArray(snowflakes, TOTAL_COMPONENT_COUNT)

    private fun bindData() {
        snowflakesVertexArray.apply {
            updateBuffer()
            setVertexAttribPointer(
                0,
                snowfallProgram.aPositionLocation,
                POSITION_COMPONENT_COUNT,
                STRIDE
            )
        }
    }

    private fun unbindData() {
        glDisableVertexAttribArray(snowfallProgram.aPositionLocation)
    }

    fun draw(mvpMatrix: FloatArray, modelMatrix: FloatArray, viewProjectionMatrix: FloatArray) {
        setIdentityM(modelMatrix, 0)
        multiplyMM(mvpMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
        snowfallProgram.run {
            useProgram()
            setUniforms(mvpMatrix, Color.WHITE, textureId)
        }
        bindData()
        glEnable(GL_BLEND)
        snowflakes.forEachIndexed { index, snowflake ->
            snowflake.fall()
            snowfallProgram.setPointSize(snowflake.radius * 100f)
            glDrawArrays(GL_POINTS, index, 1)

        }
        glDisable(GL_BLEND)
        unbindData()
    }

    companion object {
        private const val TAG = "SnowfallBackground"
        private const val POSITION_COMPONENT_COUNT = 2
        private const val TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT
        private const val STRIDE = 0
    }
}
