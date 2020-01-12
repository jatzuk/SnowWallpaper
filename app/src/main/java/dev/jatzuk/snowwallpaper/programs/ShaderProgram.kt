package dev.jatzuk.snowwallpaper.programs

import android.content.Context
import android.opengl.GLES20.glUseProgram
import dev.jatzuk.snowwallpaper.util.ShaderHelper

abstract class ShaderProgram(
    context: Context,
    vertexShaderResourceId: Int,
    fragmentShaderResourceId: Int
) {

    protected val program = ShaderHelper.buildProgram(
        ShaderHelper.readShaderCodeFromResource(context, vertexShaderResourceId),
        ShaderHelper.readShaderCodeFromResource(context, fragmentShaderResourceId)
    )

    protected val uMatrix = "u_Matrix"
    protected val uColor = "u_Color"
    protected val uTextureUnit = "u_TextureUnit"
    protected val uPointSize = "u_PointSize"

    protected val aPosition = "a_Position"

    fun useProgram() {
        glUseProgram(program)
    }
}
