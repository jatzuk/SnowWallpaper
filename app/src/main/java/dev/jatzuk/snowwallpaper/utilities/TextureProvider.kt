package dev.jatzuk.snowwallpaper.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.TextureCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException

object TextureProvider {

    private val textureCache = TextureCache.getInstance()
    private const val TAG = "TextureProvider"

    fun saveImage(
        context: Context,
        textureType: TextureType,
        bitmap: Bitmap? = null,
        @DrawableRes resourceId: Int = -1
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val bmp = bitmap ?: decodeSampledBitmapFromResource(context, resourceId)
            val result = storeImage(context, bmp, textureType)

            launch(Dispatchers.Main) {
                val message =
                    if (result) context.getString(R.string.toast_image_storage_succeeded)
                    else context.getString(R.string.toast_image_storage_failed)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loadTexture(context: Context, textureType: TextureType): Bitmap? {
        return try {
            val cachedTexture = textureCache[textureType]
            if (cachedTexture != null) {
                Logger.d("got texture ${textureType.name} from cache", TAG)
                cachedTexture
            } else {
                Logger.d(
                    "requested texture ${textureType.name} not found in cache, trying to retrieve from disk",
                    TAG
                )
                BitmapFactory.decodeStream(context.openFileInput(textureType.path)).run {
                    textureCache[textureType] = this
                    this
                }
            }
        } catch (e: FileNotFoundException) {
            Logger.d("texture $textureType not found in disk using default", TAG)
            val bitmap = assignDefaultTexture(context, textureType)
            context.openFileOutput(textureType.path, Context.MODE_PRIVATE).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            textureCache[textureType] = bitmap
            bitmap
        } catch (e: IOException) {
            Logger.e("Failed to load image type: ${textureType.name} from internal storage", TAG, e)
            null
        }
    }

    fun assignDefaultTexture(context: Context, textureType: TextureType): Bitmap {
        val resourceId = when (textureType) {
            TextureType.SNOWFALL_TEXTURE -> R.drawable.texture_snowfall
            TextureType.SNOWFLAKE_TEXTURE -> R.drawable.texture_snowflake
            TextureType.BACKGROUND_IMAGE -> R.drawable.background_image
        }
        return ContextCompat.getDrawable(context, resourceId)!!.toBitmap()
    }

    fun clearStoredImages(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.filesDir.listFiles()?.forEach { it.delete() }
            } catch (e: IOException) {
                Logger.e("file cannot be deleted", TAG, e)
            }
            textureCache.clear()
        }
    }

    private suspend fun decodeSampledBitmapFromResource(
        context: Context,
        @DrawableRes resourceId: Int
    ): Bitmap = withContext(Dispatchers.IO) {
        val (height, width) = DisplayMetrics().run {
            (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(this)
            ydpi.toInt() to xdpi.toInt()
        }
        BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeResource(context.resources, resourceId, this)
            inSampleSize = calculateInSampleSize(this, width, height)
            inJustDecodeBounds = false
            BitmapFactory.decodeResource(context.resources, resourceId, this)
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
    ): Boolean = withContext(Dispatchers.IO) {
        textureCache[textureType] = bitmap
        val (compressFormat, compressQuality) =
            if (textureType == TextureType.BACKGROUND_IMAGE) Bitmap.CompressFormat.JPEG to 75
            else Bitmap.CompressFormat.PNG to 100

        context.openFileOutput(textureType.path, Context.MODE_PRIVATE).use {
            bitmap.compress(compressFormat, compressQuality, it)
        }
    }

    enum class TextureType(val path: String) {
        SNOWFALL_TEXTURE("snowfall_texture.png"),
        SNOWFLAKE_TEXTURE("snowflake_texture.png"),
        BACKGROUND_IMAGE("background_image.jpeg"),
    }
}
