package dev.jatzuk.snowwallpaper.opengl.util

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLUtils.texImage2D
import dev.jatzuk.snowwallpaper.utilities.ImageProvider
import dev.jatzuk.snowwallpaper.utilities.Logger.errorLog

private const val TAG = "TextureHelper"

fun loadTexture(context: Context, resourceId: Int = -1, imageType: ImageProvider.ImageType): Int {
    val textureObjectsIds = intArrayOf(0)
    glGenTextures(1, textureObjectsIds, 0)

    if (textureObjectsIds[0] == 0) {
        errorLog("Could not generate a new Open GL texture object", TAG)
        return 0
    }

//    val bitmap = if (resourceId == -1) {
//    } else {
//    }

    val bitmap = ImageProvider.loadImage(context, imageType = imageType)
//    val options = BitmapFactory.Options().apply { inScaled = false }
//    BitmapFactory.decodeResource(context.resources, resourceId, options)

    if (bitmap == null) {
//        errorLog("Resource ID: $resourceId could not be decoded", TAG)
        glDeleteTextures(1, textureObjectsIds, 0)
        return 0
    }

    glBindTexture(GL_TEXTURE_2D, textureObjectsIds[0])

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)

    glGenerateMipmap(GL_TEXTURE_2D)

    bitmap.recycle()

    glBindTexture(GL_TEXTURE_2D, 0)

//    logging("Texture ID: $resourceId loaded successfully", TAG)
    return textureObjectsIds[0]
}
