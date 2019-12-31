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

    protected val aPosition = "a_Position"
    protected val aTextureCoordinates = "a_TextureCoordinates"

    fun useProgram() {
        glUseProgram(program)
    }
}
