package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import android.opengl.GLES20.GL_TRIANGLE_STRIP
import android.opengl.GLES20.glDrawArrays
import android.opengl.Matrix.multiplyMM
import android.opengl.Matrix.setIdentityM
import dev.jatzuk.snowwallpaper.opengl.data.VertexArray
import dev.jatzuk.snowwallpaper.opengl.programs.BackgroundImageProgram
import dev.jatzuk.snowwallpaper.opengl.util.BYTES_PER_FLOAT
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

class BackgroundImage(
    context: Context,
    mvpMatrix: FloatArray,
    modelMatrix: FloatArray,
    viewProjectionMatrix: FloatArray
) : OpenGLSceneObject(context, mvpMatrix, modelMatrix, viewProjectionMatrix) {

    override val shaderProgram = BackgroundImageProgram(context)
    override val textureType = TextureProvider.TextureType.BACKGROUND_IMAGE

    override fun bindData() {
        vertexArray.apply {
            setVertexAttribPointer(
                0,
                shaderProgram.aPositionLocation,
                POSITION_COMPONENT_COUNT,
                STRIDE
            )

            setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                shaderProgram.aTextureLocation,
                TEXTURE_COMPONENT_COUNT,
                STRIDE
            )
        }
    }

    override fun draw() {
        shaderProgram.useProgram()
        bindData()
        setIdentityM(modelMatrix, 0)
        multiplyMM(mvpMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
        shaderProgram.setUniforms(mvpMatrix, textureId)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        unbindData()
    }

    // stub
    override fun bindObjectArray(context: Context) {}

    override fun getObjectCount(): Int = 1

    override fun updateVertexArray(): VertexArray = VertexArray(
        floatArrayOf(
            -1f, 1f, 1f, 0f, 0f,
            -1f, -1f, 1f, 0f, 1f,
            1f, 1f, 1f, 1f, 0f,
            1f, -1f, 1f, 1f, 1f
        ),
        TOTAL_COMPONENT_COUNT
    )

    companion object {
        const val TAG = "BackgroundImage"
        private const val POSITION_COMPONENT_COUNT = 3
        private const val TEXTURE_COMPONENT_COUNT = 2
        private const val TOTAL_COMPONENT_COUNT = (POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT) +
                (TEXTURE_COMPONENT_COUNT * BYTES_PER_FLOAT)
        private const val STRIDE =
            (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }
}
