package dev.jatzuk.snowwallpaper.utilities

import android.graphics.Bitmap

class TextureCache(maxEntries: Int = 3) {

    private var hashMap = HashMap<ImageProvider.ImageType, Bitmap?>(maxEntries, 1f)

    private fun add(key: ImageProvider.ImageType, value: Bitmap?) {
//        val prev = hashMap[key]
//        prev?.recycle()
        hashMap[key] = value
    }

    private fun getValue(key: ImageProvider.ImageType): Bitmap? = hashMap[key]

    fun remove(key: ImageProvider.ImageType): Bitmap? = hashMap.remove(key)

    fun clear() {
        hashMap.forEach { it.value?.recycle() }
        hashMap.clear()
    }

    operator fun set(key: ImageProvider.ImageType, value: Bitmap?) {
        add(key, value)
    }

    operator fun get(key: ImageProvider.ImageType): Bitmap? {
        return getValue(key)
    }
}
