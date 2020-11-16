package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import android.opengl.GLES20.glDisableVertexAttribArray
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.data.preferences.TextureCache
import dev.jatzuk.snowwallpaper.opengl.data.VertexArray
import dev.jatzuk.snowwallpaper.opengl.programs.ShaderProgram
import dev.jatzuk.snowwallpaper.opengl.util.loadTextureToOpenGL
import dev.jatzuk.snowwallpaper.utilities.Logger
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
    private var textureGenerationId = -1
    protected abstract val textureType: TextureProvider.TextureType

    protected var objectsCount = 0
        private set

    protected lateinit var vertexArray: VertexArray
        private set

    protected val preferenceRepository = PreferenceRepository.getInstance(context)

    private fun updateTexture(context: Context) {
        if (textureGenerationId == -1) {
            Logger.d("$textureType first bitmap initialization", TAG)
            loadTextureToOpenGL(context, textureType)?.run {
                textureId = first
                textureGenerationId = second
            } ?: throw IllegalArgumentException("OpenGL binding for $textureType failed")
        } else {
            if (textureGenerationId != TextureCache.getInstance()[textureType]?.generationId) {
                Logger.d("$textureType has changed, allocating new", TAG)
                loadTextureToOpenGL(context, textureType)?.run {
                    textureId = first
                    textureGenerationId = second
                }
            } else {
                Logger.d("$textureType - actual set in OpenGL, using existing", TAG)
            }
        }
    }

    protected abstract fun getObjectCount(): Int

    abstract fun bindObjectArray(context: Context)

    protected abstract fun updateVertexArray(): VertexArray

    /**
     * for [Snowflake] updates when no gl changes
     * */
    protected open fun updateContent() {}

    fun updateOpenGLValues(context: Context) {
        updateTexture(context)

        val updatedObjectsCount = getObjectCount()
        if (updatedObjectsCount != objectsCount) {
            Logger.d("$textureType updated size, reallocating values", TAG)
            objectsCount = updatedObjectsCount
            bindObjectArray(context)
            vertexArray = updateVertexArray()
        } else {
            Logger.d("$textureType previously size, no need to reallocate", TAG)

        }

        updateContent()
    }

    protected abstract fun bindData()

    protected fun unbindData() {
        glDisableVertexAttribArray(shaderProgram.aPositionLocation)
    }

    abstract fun draw()

    companion object {

        private const val TAG = "OpenGLSceneObject"
    }
}
