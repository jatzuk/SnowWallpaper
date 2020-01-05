package dev.jatzuk.snowwallpaper.programs

import android.content.Context
import android.opengl.GLES20.*
import dev.jatzuk.snowwallpaper.R

class SnowfallProgram(context: Context) :
    ShaderProgram(context, R.raw.snowfall_vertex_shader, R.raw.snowfall_fragment_shader) {

    private val uMatrixLocation = glGetUniformLocation(program, uMatrix)
//    private val uTimeLocation = glGetUniformLocation(program, uTime)
//    private val uTextureUnitLocation = glGetUniformLocation(program, uTextureUnit)
    private val uColorLocation = glGetUniformLocation(program, uColor)

    val aPositionLocation = glGetAttribLocation(program, aPosition)
//    val aColorLocation = glGetAttribLocation(program, aColor)
//    val aDirectionVectorLocation = glGetAttribLocation(program, aDirectionVector)
//    val aSnowflakeStartTimeLocation = glGetAttribLocation(program, aSnowflakeStartTime)

//    fun setUniforms(matrix: FloatArray, elapsedTime: Float, textureId: Int) {
//        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
//        glUniform1f(uTimeLocation, elapsedTime)
//        glActiveTexture(GL_TEXTURE0)
//        glBindTexture(GL_TEXTURE_2D, textureId)
//        glUniform1i(uTextureUnitLocation, 0)
//    }

    fun setUniforms(matrix: FloatArray) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        glUniform4f(uColorLocation, 1f, 1f, 1f, 1f)

//        glActiveTexture(GL_TEXTURE0)
//        glBindTexture(GL_TEXTURE_2D, textureId)
    }
}
