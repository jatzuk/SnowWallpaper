package dev.jatzuk.snowwallpaper.utilities

import android.graphics.Bitmap
import android.util.LruCache

class TextureCache : LruCache<String, Bitmap>(CACHE_SIZE) {

    override fun sizeOf(key: String, bitmap: Bitmap): Int {
        return bitmap.byteCount / 1024
    }

    fun putBitmap(key: String, bitmap: Bitmap): Bitmap? {
        val result = super.put(key, bitmap)
        if (result != bitmap) {
//      todo notify that new texture inserted in the cache
        }
        return result
    }

    companion object {
        private const val CACHE_SIZE = 4 * 1024 * 1024
    }
}
