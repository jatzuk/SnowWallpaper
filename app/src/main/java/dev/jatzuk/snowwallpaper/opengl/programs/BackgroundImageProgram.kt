package dev.jatzuk.snowwallpaper.opengl.programs

import android.content.Context
import android.opengl.GLES20.*
import dev.jatzuk.snowwallpaper.R

class BackgroundImageProgram(context: Context) :
    ShaderProgram(
        context,
        R.raw.background_image_vertex_shader,
        R.raw.background_image_fragment_shader
    ) {

    private val uMatrixLocation = glGetUniformLocation(program, uMatrix)
    private val uTextureUnitLocation = glGetUniformLocation(program, uTextureUnit)

    val aTextureLocation = glGetAttribLocation(program, aTexture)

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        setNormalizedCoordinates()
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(uTextureUnitLocation, 0)
    }
}
