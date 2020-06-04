package dev.jatzuk.snowwallpaper.utilities

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.TextureCache
import dev.jatzuk.snowwallpaper.utilities.Logger.errorLog
import dev.jatzuk.snowwallpaper.utilities.Logger.logging
import kotlinx.coroutines.*
import java.io.FileNotFoundException
import java.io.IOException

object TextureProvider {

    private val textureCache = TextureCache.getInstance()
    private const val TAG = "ImageProvider"

    fun saveImage(
        context: Context,
        textureType: TextureType,
        bitmap: Bitmap? = null,
        @DrawableRes resourceId: Int = -1
    ): Boolean {
        val (height, width) = DisplayMetrics().run {
            (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(this)
            ydpi.toInt() to xdpi.toInt()
        }

//        CoroutineScope(Dispatchers.IO).launch {
        runBlocking {

            val bitmapResolvingJob = async {
                bitmap ?: decodeSampledBitmapFromResource(
                    context.resources,
                    resourceId,
                    height,
                    width
                )
            }

            val storeJob = async {
                storeImage(context, bitmapResolvingJob.await(), textureType)
            }
        }

//        }
        return true
    }

    // todo(side thread?)
    fun loadTexture(context: Context, textureType: TextureType): Bitmap? {
        return try {
            val cachedTexture = textureCache[textureType]
            if (cachedTexture != null) {
                logging("got texture ${textureType.name} from cache", TAG)
                cachedTexture
            } else {
                logging(
                    "requested texture ${textureType.name} not found in cache, trying to retrieve from disk",
                    TAG
                )
                BitmapFactory.decodeStream(context.openFileInput(textureType.path)).run {
                    textureCache[textureType] = this
                    this
                }
            }
        } catch (e: FileNotFoundException) {
            logging("texture $textureType not found in disk using default", TAG)
            val bitmap = getDefaultTextureByImageType(context, textureType)
            context.openFileOutput(textureType.path, Context.MODE_PRIVATE).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
//            CoroutineScope(Dispatchers.IO).launch {
//                storeImage(context, bitmap, imageType)
//            }
            bitmap
        } catch (e: IOException) {
            errorLog("Failed to load image type: ${textureType.name} from internal storage", TAG, e)
            null
        }
    }

    fun getDefaultTextureByImageType(context: Context, textureType: TextureType): Bitmap {
        val resourceId = when (textureType) {
            TextureType.SNOWFALL_TEXTURE -> R.drawable.texture_snowfall
            TextureType.SNOWFLAKE_TEXTURE -> R.drawable.texture_snowflake
            TextureType.BACKGROUND_IMAGE -> R.drawable.background_image
        }
        val bitmap = ContextCompat.getDrawable(context, resourceId)!!.toBitmap()
        textureCache[textureType] = bitmap
        return bitmap
    }

    fun clearStoredImages(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.filesDir.listFiles()?.forEach { it.delete() }
            } catch (e: IOException) {
                errorLog("file cannot be deleted", TAG, e)
            }
            textureCache.clear()
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
        textureType: TextureType
    ): Boolean /*= withContext(Dispatchers.IO)*/ {
        textureCache[textureType] = bitmap
        context.openFileOutput(textureType.path, Context.MODE_PRIVATE).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        return true
    }

    enum class TextureType(val path: String) {
        SNOWFALL_TEXTURE("snowfall_texture.png"),
        SNOWFLAKE_TEXTURE("snowflake_texture.png"),
        BACKGROUND_IMAGE("background_image.png"),
    }
}
