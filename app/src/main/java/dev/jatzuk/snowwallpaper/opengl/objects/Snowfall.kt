package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20.*
import android.opengl.Matrix.multiplyMM
import android.opengl.Matrix.setIdentityM
import dev.jatzuk.snowwallpaper.opengl.data.SnowflakeVertexArray
import dev.jatzuk.snowwallpaper.opengl.data.VertexArray
import dev.jatzuk.snowwallpaper.opengl.programs.SnowfallProgram
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

class Snowfall(
    context: Context,
    mvpMatrix: FloatArray,
    modelMatrix: FloatArray,
    viewProjectionMatrix: FloatArray
) : OpenGLSceneObject(context, mvpMatrix, modelMatrix, viewProjectionMatrix) {

    override val shaderProgram = SnowfallProgram(context)
    override val textureType = TextureProvider.TextureType.SNOWFALL_TEXTURE
    private lateinit var objectArray: Array<Snowflake>

    override fun bindData() {
        vertexArray.apply {
            updateBuffer()
            setVertexAttribPointer(
                0,
                shaderProgram.aPositionLocation,
                POSITION_COMPONENT_COUNT,
                STRIDE
            )
        }
    }

    override fun draw() {
        setIdentityM(modelMatrix, 0)
        multiplyMM(mvpMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
        shaderProgram.run {
            useProgram()
            setUniforms(mvpMatrix, Color.WHITE, textureId)
        }
        bindData()
        glEnable(GL_BLEND)
        objectArray.forEachIndexed { index, snowflake ->
            snowflake.fall()
            shaderProgram.setPointSize(snowflake.radius.toFloat())
            glDrawArrays(GL_POINTS, index, 1)
        }
        glDisable(GL_BLEND)
        unbindData()
    }

    override fun bindObjectArray(context: Context) {
        objectArray = Array(objectsCount) { Snowflake(context) }
    }

    override fun getObjectCount(): Int = preferenceRepository.getSnowfallLimit()

    override fun updateVertexArray(): VertexArray =
        SnowflakeVertexArray(objectArray, TOTAL_COMPONENT_COUNT)

    companion object {
        const val TAG = "Snowfall"
        private const val POSITION_COMPONENT_COUNT = 2
        private const val TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT
        private const val STRIDE = 0
    }
}
