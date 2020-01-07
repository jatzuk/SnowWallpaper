package dev.jatzuk.snowwallpaper.objects

import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDrawArrays
import dev.jatzuk.snowwallpaper.data.VertexArray
import dev.jatzuk.snowwallpaper.programs.SnowfallProgram

class SnowfallBackground(private val snowfallProgram: SnowfallProgram) {
    private val snowflakesLimit = 150 // todo(load from preferences)
    private val snowflakes = Array(snowflakesLimit) { Snowflake() }
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
            snowfallProgram.applyPointSize(snowflake.radius)
            glDrawArrays(GL_POINTS, 0, index + 1)
        }
    }

    companion object {
        private val TAG = SnowfallBackground::class.java.simpleName
        const val POSITION_COMPONENT_COUNT = 2
        private const val COLOR_COMPONENT_COUNT = 2
        private const val TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT
        private const val STRIDE = 0//TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT
    }
}
