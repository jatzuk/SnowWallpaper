package dev.jatzuk.snowwallpaper.utilities

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.utilities.Logger.errorLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

object ImageProvider {

    private const val TAG = "ImageProvider"
    private const val BACKGROUND_IMAGE = "background.png"
    private const val THUMBNAIL_IMAGE = "thumbnail.png"

    fun saveBackgroundImage(
        context: Context,
        uri: Uri? = null,
        @DrawableRes resourceId: Int = -1
    ) {
        val (height, width) = DisplayMetrics().run {
            (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(this)
            ydpi.toInt() to xdpi.toInt()
        }
        CoroutineScope(Dispatchers.Main).launch {
            val bitmap =
                if (uri != null) decodeSampledBitmapFromUri(context, uri, height, width)
                else decodeSampledBitmapFromResource(context.resources, resourceId, height, width)
            val result = storeImage(context, bitmap)
            val message =
                if (result) context.getString(R.string.image_storage_successed)
                else context.getString(R.string.image_storage_failed)
            if (result) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun loadThumbnailImage(context: Context): Bitmap? {
        return try {
            BitmapFactory.decodeStream(context.openFileInput(THUMBNAIL_IMAGE))
        } catch (e: IOException) {
            errorLog("failed to load thumbnail image", TAG, e)
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

    private suspend fun decodeSampledBitmapFromUri(
        context: Context,
        uri: Uri,
        requiredHeight: Int,
        requiredWidth: Int
    ): Bitmap = withContext(Dispatchers.IO) {
        BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, this)
            inSampleSize = calculateInSampleSize(this, requiredHeight, requiredWidth)
            inJustDecodeBounds = false
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, this)!!
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
        bitmap: Bitmap
    ): Boolean = withContext(Dispatchers.IO) {
        context.openFileOutput(BACKGROUND_IMAGE, Context.MODE_PRIVATE).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        val dstSize = context.resources.getDimensionPixelSize(R.dimen.thumbnail_size)
        context.openFileOutput(THUMBNAIL_IMAGE, Context.MODE_PRIVATE).use {
            Bitmap.createScaledBitmap(bitmap, dstSize, dstSize, false)
                .compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
