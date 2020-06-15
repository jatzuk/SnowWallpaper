package dev.jatzuk.snowwallpaper.data.preferences

import android.graphics.Bitmap
import dev.jatzuk.snowwallpaper.utilities.Logger.logging
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

class TextureCache private constructor(entriesCount: Int) {

    private val hashMap = HashMap<TextureProvider.TextureType, Bitmap?>(entriesCount, 1f)

    private fun add(key: TextureProvider.TextureType, value: Bitmap?) {
        hashMap[key] = value
    }

    private fun getValue(key: TextureProvider.TextureType): Bitmap? = hashMap[key]

    fun remove(key: TextureProvider.TextureType): Bitmap? = hashMap.remove(key)

    /**
     * @see <a href="https://developer.android.com/topic/performance/graphics/manage-memory#recycle">link</a>
     * */
    fun clear() {
        // expecting proper bitmap native memory management instead of manual bitmap.recycle()
        logging("texture cache is cleared", TAG)
        hashMap.clear()
    }

    operator fun set(key: TextureProvider.TextureType, value: Bitmap?) {
        add(key, value)
    }

    operator fun get(key: TextureProvider.TextureType): Bitmap? {
        return getValue(key)
    }

    companion object {
        @Volatile
        private var instance: TextureCache? = null
        private const val TAG = "TextureCache"

        fun getInstance(entriesCount: Int = 3): TextureCache =
            instance ?: synchronized(this) {
                instance ?: TextureCache(entriesCount).also { instance = it }
            }
    }
}
