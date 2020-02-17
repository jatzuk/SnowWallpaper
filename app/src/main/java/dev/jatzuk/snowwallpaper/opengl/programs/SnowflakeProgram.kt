package dev.jatzuk.snowwallpaper.opengl.programs

import android.content.Context
import android.opengl.GLES20.*
import dev.jatzuk.snowwallpaper.R

class SnowflakeProgram(context: Context) :
    ShaderProgram(context, R.raw.cube_vertex_shader, R.raw.cube_fragment_shader) {

    private val uMatrixLocation = glGetUniformLocation(program, uMatrix)
    val aPositionLocation = glGetAttribLocation(program, aPosition)
    val uColorLocation = glGetUniformLocation(program, uColor)

    fun setUniforms(matrix: FloatArray) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        glUniform4f(uColorLocation, 0f, 1f, 0f, 1f)
//        glUniform4f(
//            uColorLocation,
//            Color.red(color) / 255f,
//            Color.green(color) / 255f,
//            Color.blue(color) / 255f,
//            1f
//        )

    }
}
