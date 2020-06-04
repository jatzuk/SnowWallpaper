package dev.jatzuk.snowwallpaper.opengl.util

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLUtils.texImage2D
import dev.jatzuk.snowwallpaper.utilities.Logger.errorLog
import dev.jatzuk.snowwallpaper.utilities.Logger.logging
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

private const val TAG = "TextureHelper"

fun loadTextureToOpenGL(context: Context, textureType: TextureProvider.TextureType): Int {
    val textureObjectsIds = intArrayOf(0)
    glGenTextures(1, textureObjectsIds, 0)

    if (textureObjectsIds[0] == 0) {
        errorLog("Could not generate a new Open GL texture object", TAG)
        return 0
    }

    val bitmap = TextureProvider.loadTexture(context, textureType)
    if (bitmap == null) {
        errorLog("Bitmap type ${textureType.name} could not be loaded", TAG)
        glDeleteTextures(1, textureObjectsIds, 0)
        return 0
    }

    glBindTexture(GL_TEXTURE_2D, textureObjectsIds[0])

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)

    glGenerateMipmap(GL_TEXTURE_2D)
    glBindTexture(GL_TEXTURE_2D, 0)

//    bitmap.recycle() // todo throws IllegalArgumentException involves by twice call inside Renderer
    logging("Texture type: ${textureType.name} loaded successfully", TAG)

    return textureObjectsIds[0]
}
