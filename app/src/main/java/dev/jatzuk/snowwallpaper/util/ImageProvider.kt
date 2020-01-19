package dev.jatzuk.snowwallpaper.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.util.Logger.errorLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

object ImageProvider {
    private const val TAG = "ImageProvider"
    private const val BACKGROUND_IMAGE = "background.jpg"
    private const val THUMBNAIL_IMAGE = "thumbnail.jpg"

    fun saveBackgroundImage(@DrawableRes resourceId: Int, context: Context): Boolean {
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        return try {
            CoroutineScope(Dispatchers.IO).launch {
                context.openFileOutput(BACKGROUND_IMAGE, Context.MODE_PRIVATE).use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            }
            val dstSize = context.resources.getDimensionPixelSize(R.dimen.thumbnail_size)
            CoroutineScope(Dispatchers.IO).launch {
                context.openFileOutput(THUMBNAIL_IMAGE, Context.MODE_PRIVATE).use {
                    Bitmap.createScaledBitmap(bitmap, dstSize, dstSize, false)
                        .compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            }
            true
        } catch (e: IOException) {
            errorLog("failed to save background image $resourceId", TAG, e)
            false
        }
    }

    fun loadThumbnailImage(context: Context): Bitmap? {
        return try {
            BitmapFactory.decodeStream(context.openFileInput(THUMBNAIL_IMAGE))
        } catch (e: IOException) {
            errorLog("failed to load thumbnail image")
            null
        }
    }

    fun loadBackgroundImage(context: Context): Bitmap? { // todo(side thread?)
        return try {
            BitmapFactory.decodeStream(context.openFileInput(BACKGROUND_IMAGE))
        } catch (e: IOException) {
            errorLog("failed to load background image from storage", TAG, e)
            null
        }
    }
}
