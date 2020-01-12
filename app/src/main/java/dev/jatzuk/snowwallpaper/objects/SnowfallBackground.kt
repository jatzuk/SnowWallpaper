package dev.jatzuk.snowwallpaper.objects

import android.content.Context
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDrawArrays
import androidx.preference.PreferenceManager
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.VertexArray
import dev.jatzuk.snowwallpaper.programs.SnowfallProgram

class SnowfallBackground(private val snowfallProgram: SnowfallProgram, context: Context) {

    private val snowflakesLimit = PreferenceManager.getDefaultSharedPreferences(context).getInt(
        context.getString(R.string.background_snowflakes_limit_key), 80
    )
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
        private val TAG = SnowfallBackground::class.java.simpleName
        const val POSITION_COMPONENT_COUNT = 2
        const val TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT
        private const val STRIDE = 0
    }
}
