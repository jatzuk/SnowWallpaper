package dev.jatzuk.snowwallpaper.util

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils.texImage2D
import dev.jatzuk.snowwallpaper.util.Logger.errorLog
import dev.jatzuk.snowwallpaper.util.Logger.logging

private const val TAG = "TextureHelper"

fun loadTexture(context: Context, resourceId: Int): Int {
    val textureObjectsIds = intArrayOf(0)
    glGenTextures(1, textureObjectsIds, 0)

    if (textureObjectsIds[0] == 0) {
        errorLog(TAG, "Could not generate a new Open GL texture object")
        return 0
    }

    val options = BitmapFactory.Options().apply { inScaled = false }
    val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

    if (bitmap == null) {
        errorLog(TAG, "Resource ID: $resourceId could not be decoded")
        glDeleteTextures(1, textureObjectsIds, 0)
        return 0
    }

    glBindTexture(GL_TEXTURE_2D, textureObjectsIds[0])

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)

    glGenerateMipmap(GL_TEXTURE_2D)

    bitmap.recycle()

    glBindTexture(GL_TEXTURE_2D, 0)

    logging(TAG, "Texture ID: $resourceId loaded successfully")
    return textureObjectsIds[0]
}
