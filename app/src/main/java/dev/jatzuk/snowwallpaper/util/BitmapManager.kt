package dev.jatzuk.snowwallpaper.util
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.util.LruCache
//import dev.jatzuk.snowwallpaper.wallpaper.GLWallpaperService.Companion.FLAKES_COUNT
//import dev.jatzuk.snowwallpaper.wallpaper.GLWallpaperService.Companion.FLAKES_COUNT_BIG
//import dev.jatzuk.snowwallpaper.wallpaper.GLWallpaperService.Companion.log
//
//object BitmapManager {
//    const val FLAKE_SIZE_LOWER = 300f
//    private const val FLAKE_SIZE_UPPER = 400f
//    private lateinit var bitmap: Bitmap
//    private val bitmapCache = LruCache<String, Bitmap>(FLAKES_COUNT_BIG)
//
//    init {
//        log("bitmapCache: ${bitmapCache.maxSize()}")
//    }
//
//    fun getScaledBitmap(radius: Float): Bitmap? = bitmapCache.get(radius.toInt().toString())
//
//    fun generateBitmaps(context: Context, resId: Int) {
//        bitmap = BitmapFactory.decodeResource(context.resources, resId)
//        repeat(FLAKES_COUNT_BIG/*(FLAKE_SIZE_UPPER - FLAKE_SIZE_LOWER).toInt() / 50 + 1*/) {
//            val radius = FLAKE_SIZE_LOWER.toInt() + (it * FLAKES_COUNT * 10)
//            bitmapCache.put(
//                "$radius", Bitmap.createScaledBitmap(bitmap, radius, radius, false)
//            )
//        }
//    }
//}
