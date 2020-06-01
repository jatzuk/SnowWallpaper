package dev.jatzuk.snowwallpaper.utilities

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.utilities.Logger.errorLog
import dev.jatzuk.snowwallpaper.utilities.Logger.logging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException

object ImageProvider {

    private val textureCache = TextureCache()
    private const val TAG = "ImageProvider"

    fun saveImage(
        context: Context,
        imageType: ImageType,
        bitmap: Bitmap? = null,
        @DrawableRes resourceId: Int = -1
    ) {
        val (height, width) = DisplayMetrics().run {
            (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(this)
            ydpi.toInt() to xdpi.toInt()
        }
        CoroutineScope(Dispatchers.Main).launch {
            val bmp = bitmap ?: decodeSampledBitmapFromResource(
                context.resources,
                resourceId,
                height,
                width
            )
            val result = storeImage(context, bmp, imageType)
            val message =
                if (result) context.getString(R.string.toast_image_storage_succeeded)
                else context.getString(R.string.toast_image_storage_failed)
            if (result) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // todo(side thread?)
    fun loadTexture(context: Context, imageType: ImageType): Bitmap? {
        return try {
            val cachedTexture = textureCache[imageType]
            if (cachedTexture != null) {
                logging("got texture ${imageType.name} from cache", TAG)
                cachedTexture
            } else {
                logging(
                    "requested texture ${imageType.name} not found in cache, trying to retrieve from disk",
                    TAG
                )
                BitmapFactory.decodeStream(context.openFileInput(imageType.path)).run {
                    textureCache[imageType] = this
                    this
                }
            }
        } catch (e: FileNotFoundException) {
            logging("texture $imageType not found in disk using default", TAG)
            val bitmap = loadDefaultTextureForImageType(context, imageType)
            context.openFileOutput(imageType.path, Context.MODE_PRIVATE).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
//            CoroutineScope(Dispatchers.IO).launch {
//                storeImage(context, bitmap, imageType)
//            }
            bitmap
        } catch (e: IOException) {
            errorLog("Failed to load image type: ${imageType.name} from internal storage", TAG, e)
            null
        }
    }

    fun loadDefaultTextureForImageType(context: Context, imageType: ImageType): Bitmap {
        val resourceId = when (imageType) {
            ImageType.SNOWFALL_TEXTURE -> R.drawable.texture_snowfall
            ImageType.SNOWFLAKE_TEXTURE -> R.drawable.texture_snowflake
            ImageType.BACKGROUND_IMAGE -> R.drawable.background_image
        }
        val bitmap = ContextCompat.getDrawable(context, resourceId)!!.toBitmap()
        textureCache[imageType] = bitmap
        return bitmap
    }

    fun clearStoredImages(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.filesDir.listFiles()?.forEach { it.delete() }
            } catch (e: IOException) {
                errorLog("file cannot be deleted", TAG, e)
            }
            clearCache()
        }
    }

    private suspend fun decodeSampledBitmapFromResource(
        resources: Resources,
        @DrawableRes resourceId: Int,
        requiredHeight: Int,
        requiredWidth: Int
    ): Bitmap = withContext(Dispatchers.IO) {
        BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeResource(resources, resourceId, this)
            inSampleSize = calculateInSampleSize(this, requiredWidth, requiredHeight)
            inJustDecodeBounds = false
            BitmapFactory.decodeResource(resources, resourceId, this)
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        requiredHeight: Int,
        requiredWidth: Int
    ): Int {
        val (height, width) = options.run { outHeight to outWidth }
        var sampleSize = 1

        if (height > requiredHeight || width > requiredWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / sampleSize >= requiredHeight && halfWidth / sampleSize >= requiredWidth) {
                sampleSize *= 2
            }
        }

        return sampleSize
    }

    private suspend fun storeImage(
        context: Context,
        bitmap: Bitmap,
        imageType: ImageType
    ): Boolean = withContext(Dispatchers.IO) {
        textureCache[imageType] = bitmap
        context.openFileOutput(imageType.path, Context.MODE_PRIVATE).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    fun getBitmapFromCache(imageType: ImageType): Bitmap? {
        return textureCache[imageType]
    }

    fun removeFromCache(imageType: ImageType): Boolean {
        return textureCache.remove(imageType) != null
    }

    fun clearCache() {
        textureCache.clear()
    }

    fun getTextureCache(): TextureCache = textureCache//TextureCache = textureCache

    enum class ImageType(val path: String) {
        SNOWFALL_TEXTURE("snowfall_texture.png"),
        SNOWFLAKE_TEXTURE("snowflake_texture.png"),
        BACKGROUND_IMAGE("background_image.png"),
    }
}
