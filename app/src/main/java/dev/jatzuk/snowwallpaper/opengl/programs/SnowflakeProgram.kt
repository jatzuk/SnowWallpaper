package dev.jatzuk.snowwallpaper.opengl.programs

import android.content.Context
import android.opengl.GLES20.*
import dev.jatzuk.snowwallpaper.R

class SnowflakeProgram(context: Context) :
    ShaderProgram(context, R.raw.snowflake_vertex_shader, R.raw.snowflake_fragment_shader) {

    private val uMatrixLocation = glGetUniformLocation(program, uMatrix)
    private val uTextureUnitLocation = glGetUniformLocation(program, uTextureUnit)
//    private val uColorLocation = glGetUniformLocation(program, uColor)
//    private val uPointSizeLocation = glGetUniformLocation(program, uPointSize)

    val aPositionLocation = glGetAttribLocation(program, aPosition)

    val aTextureLocation = glGetAttribLocation(program, "a_Texture")

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
//        glUniform4f(uColorLocation, 0f, 1f, 0f, 1f)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(uTextureUnitLocation, 0)
    }

    fun setPointSize(radius: Float) {
//        glUniform1f(uPointSizeLocation, radius)
    }
}
