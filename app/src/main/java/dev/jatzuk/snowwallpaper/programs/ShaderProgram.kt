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
//    protected val uTime = "u_Time"
    protected val uColor = "u_Color"
    protected val uTextureUnit = "u_TextureUnit"

    protected val aPosition = "a_Position"
//    protected val aColor = "a_Color"
//    protected val aDirectionVector = "a_DirectionVector"
//    protected val aSnowflakeStartTime = "a_SnowflakeStartTime"
//    protected val aTextureCoordinates = "a_TextureCoordinates"

    fun useProgram() {
        glUseProgram(program)
    }
}
