package dev.jatzuk.snowwallpaper.opengl.programs

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20.*
import dev.jatzuk.snowwallpaper.R

class SnowfallProgram(context: Context) :
    ShaderProgram(context, R.raw.snowfall_vertex_shader, R.raw.snowfall_fragment_shader) {

    private val uMatrixLocation = glGetUniformLocation(program, uMatrix)
    private val uTextureUnitLocation = glGetUniformLocation(program, uTextureUnit)
    private val uColorLocation = glGetUniformLocation(program, uColor)
    private val uPointSizeLocation = glGetUniformLocation(program, uPointSize)

    val aPositionLocation = glGetAttribLocation(program, aPosition)

    fun setUniforms(matrix: FloatArray, color: Int, textureId: Int) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        glUniform4f(
            uColorLocation,
            Color.red(color) / 255f,
            Color.green(color) / 255f,
            Color.blue(color) / 255f,
            1f
        )

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(uTextureUnitLocation, 0)
    }

    fun setPointSize(snowflakeRadius: Float) {
        glUniform1f(uPointSizeLocation, snowflakeRadius)
    }
}
