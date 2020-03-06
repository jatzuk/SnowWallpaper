package dev.jatzuk.snowwallpaper.opengl.programs

import android.content.Context
import android.opengl.GLES20.*
import dev.jatzuk.snowwallpaper.opengl.SnowfallRenderer
import dev.jatzuk.snowwallpaper.opengl.util.ShaderHelper

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

    private val uScreenWidth = "u_ScreenWidth"
    private val uScreenHeight = "u_ScreenHeight"

    private val uScreenWidthLocation = glGetUniformLocation(program, uScreenWidth)
    private val uScreenHeightLocation = glGetUniformLocation(program, uScreenHeight)

    fun useProgram() {
        glUseProgram(program)
    }

    protected fun setNormalizedCoordinates() {
        glUniform1f(uScreenWidthLocation, SnowfallRenderer.width)
        glUniform1f(uScreenHeightLocation, SnowfallRenderer.height)
    }
}
