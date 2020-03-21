package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.Matrix.multiplyMM
import android.opengl.Matrix.setIdentityM
import dev.jatzuk.snowwallpaper.opengl.data.VertexArray
import dev.jatzuk.snowwallpaper.opengl.programs.BackgroundImageProgram
import dev.jatzuk.snowwallpaper.opengl.util.BYTES_PER_FLOAT
import dev.jatzuk.snowwallpaper.opengl.util.loadTexture
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

class BackgroundImage(context: Context) {

    private val backgroundImageProgram = BackgroundImageProgram(context)
    private val backgroundImageVertexArray = VertexArray(
        floatArrayOf(
            -1f, 1f, 1f, 0f, 0f,
            -1f, -1f, 1f, 0f, 1f,
            1f, 1f, 1f, 1f, 0f,
            1f, -1f, 1f, 1f, 1f
        ),
        TOTAL_COMPONENT_COUNT
    )
    private val textureId =
        loadTexture(context, imageType = ImageProvider.ImageType.BACKGROUND_IMAGE)

    private fun bindData() {
        backgroundImageVertexArray.apply {
            setVertexAttribPointer(
                0,
                backgroundImageProgram.aPositionLocation,
                POSITION_COMPONENT_COUNT,
                STRIDE
            )

            setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                backgroundImageProgram.aTextureLocation,
                TEXTURE_COMPONENT_COUNT,
                STRIDE
            )
        }
    }

    private fun unbindData() {
        glDisableVertexAttribArray(backgroundImageProgram.aPositionLocation)
    }

    fun draw(mvpMatrix: FloatArray, modelMatrix: FloatArray, viewProjectionMatrix: FloatArray) {
        backgroundImageProgram.useProgram()
        bindData()
        setIdentityM(modelMatrix, 0)
        multiplyMM(mvpMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
        backgroundImageProgram.setUniforms(mvpMatrix, textureId)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        unbindData()
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
        private const val TEXTURE_COMPONENT_COUNT = 2
        private const val TOTAL_COMPONENT_COUNT = (POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT) +
                (TEXTURE_COMPONENT_COUNT * BYTES_PER_FLOAT)
        private const val STRIDE =
            (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }
}
