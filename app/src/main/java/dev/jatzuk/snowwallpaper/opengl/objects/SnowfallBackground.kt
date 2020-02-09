package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20.*
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.data.VertexArray
import dev.jatzuk.snowwallpaper.opengl.programs.SnowfallProgram
import dev.jatzuk.snowwallpaper.opengl.util.loadTexture

class SnowfallBackground(context: Context) {

    private val snowfallProgram = SnowfallProgram(context)
    private val textureId = loadTexture(context, R.drawable.background_snowflake_texture)

    private val snowflakesLimit = PreferenceRepository.getInstance(context).getSnowfallLimit()
    private val snowflakes = Array(snowflakesLimit) { Snowflake(context) }
    private val vertexArray = VertexArray(snowflakes)

    private fun bindData() {
        vertexArray.apply {
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

    fun draw(mvpMatrix: FloatArray) {
        bindData()
        snowfallProgram.run {
            useProgram()
            setUniforms(mvpMatrix, Color.WHITE, textureId)
        }
        glEnable(GL_BLEND)
        snowflakes.forEachIndexed { index, snowflake ->
            snowflake.fall()
            snowfallProgram.setPointSize(snowflake.radius)
            glDrawArrays(GL_POINTS, 0, index + 1)

        }
        glDisable(GL_BLEND)
        unbindData()
    }

    companion object {
        private const val TAG = "SnowfallBackground"
        const val POSITION_COMPONENT_COUNT = 2
        const val TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT
        private const val STRIDE = 0
    }
}
