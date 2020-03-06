package dev.jatzuk.snowwallpaper.opengl.programs

import android.content.Context
import android.opengl.GLES20.*
import dev.jatzuk.snowwallpaper.R

class SnowflakeProgram(context: Context) :
    ShaderProgram(context, R.raw.snowflake_vertex_shader, R.raw.snowflake_fragment_shader) {

    private val uMatrixLocation = glGetUniformLocation(program, uMatrix)
    private val uTextureUnitLocation = glGetUniformLocation(program, uTextureUnit)

    val aPositionLocation = glGetAttribLocation(program, aPosition)
    val aTextureLocation = glGetAttribLocation(program, aTexture)

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        setNormalizedCoordinates()
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(uTextureUnitLocation, 0)
    }

    companion object {
        private const val aTexture = "a_Texture"
    }
}
