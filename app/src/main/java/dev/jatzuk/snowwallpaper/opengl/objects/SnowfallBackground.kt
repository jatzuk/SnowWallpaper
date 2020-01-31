package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDrawArrays
import androidx.preference.PreferenceManager
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.data.VertexArray
import dev.jatzuk.snowwallpaper.opengl.programs.SnowfallProgram

class SnowfallBackground(private val snowfallProgram: SnowfallProgram, context: Context) {

    private val snowflakesLimit = PreferenceRepository.getInstance(context).getSnowfallLimit()
    private val snowflakes = Array(snowflakesLimit) { Snowflake(context) }
    private val vertexArray = VertexArray(snowflakes)

    fun bindData() {
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

    fun draw() {
        snowflakes.forEachIndexed { index, snowflake ->
            snowflake.fall()
            snowfallProgram.setPointSize(snowflake.radius)
            glDrawArrays(GL_POINTS, 0, index + 1)
        }
    }

    companion object {
        private const val TAG = "SnowfallBackground"
        const val POSITION_COMPONENT_COUNT = 2
        const val TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT
        private const val STRIDE = 0
    }
}
