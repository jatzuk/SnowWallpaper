package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import android.opengl.GLES20.glDisableVertexAttribArray
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.data.VertexArray
import dev.jatzuk.snowwallpaper.opengl.programs.ShaderProgram
import dev.jatzuk.snowwallpaper.opengl.util.loadTextureToOpenGL
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

abstract class OpenGLSceneObject(
    context: Context,
    protected val mvpMatrix: FloatArray,
    protected val modelMatrix: FloatArray,
    protected val viewProjectionMatrix: FloatArray
) {

    protected abstract val shaderProgram: ShaderProgram
    protected var textureId = -1
        private set
    protected abstract val textureType: TextureProvider.TextureType

    protected var objectsCount = 0
        private set

    protected lateinit var vertexArray: VertexArray
        private set

    protected val preferenceRepository = PreferenceRepository.getInstance(context)

    private fun updateTexture(context: Context) {
        textureId = loadTextureToOpenGL(context, textureType)
    }

    protected abstract fun getObjectCount(): Int

    abstract fun bindObjectArray(context: Context)

    protected abstract fun updateVertexArray(): VertexArray

    fun updateValues() {
        updateTexture()
        objectsCount = getObjectCount()
        bindObjectArray()
        vertexArray = updateVertexArray()
    }

    protected abstract fun bindData()

    protected fun unbindData() {
        glDisableVertexAttribArray(shaderProgram.aPositionLocation)
    }

    abstract fun draw()
}
