package dev.jatzuk.snowwallpaper.opengl.util

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLUtils.texImage2D
import dev.jatzuk.snowwallpaper.utilities.Logger
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

private const val TAG = "TextureHelper"

fun loadTextureToOpenGL(
    context: Context,
    textureType: TextureProvider.TextureType
): Pair<Int, Int>? {
    val textureObjectsIds = intArrayOf(0)
    glGenTextures(1, textureObjectsIds, 0)

    if (textureObjectsIds[0] == 0) {
        Logger.e("Could not generate a new Open GL texture object", TAG)
        return null
    }

    val bitmap = TextureProvider.loadTexture(context, textureType)
    if (bitmap == null) {
        Logger.e("Bitmap type ${textureType.name} could not be loaded", TAG)
        glDeleteTextures(1, textureObjectsIds, 0)
        return null
    }

    glBindTexture(GL_TEXTURE_2D, textureObjectsIds[0])

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)

    glGenerateMipmap(GL_TEXTURE_2D)
    glBindTexture(GL_TEXTURE_2D, 0)
    Logger.d("Bitmap generationID: ${bitmap.generationId}", TAG)
    Logger.d("Texture type: ${textureType.name} loaded successfully", TAG)

    return textureObjectsIds[0] to bitmap.generationId
}
