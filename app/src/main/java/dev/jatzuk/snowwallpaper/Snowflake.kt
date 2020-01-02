package dev.jatzuk.snowwallpaper
//
//import android.graphics.*
//import dev.jatzuk.snowwallpaper.util.BitmapManager.FLAKE_SIZE_LOWER
//import dev.jatzuk.snowwallpaper.wallpaper.GLWallpaperService.Companion.FLAKES_COUNT_BIG
//import dev.jatzuk.snowwallpaper.wallpaper.GLWallpaperService.Companion.displayDebugInfo
//import dev.jatzuk.snowwallpaper.wallpaper.GLWallpaperService.Companion.height
//import dev.jatzuk.snowwallpaper.wallpaper.GLWallpaperService.Companion.log
//import dev.jatzuk.snowwallpaper.wallpaper.GLWallpaperService.Companion.pitch
//import dev.jatzuk.snowwallpaper.wallpaper.GLWallpaperService.Companion.roll
//import dev.jatzuk.snowwallpaper.wallpaper.GLWallpaperService.Companion.width
//import dev.jatzuk.snowwallpaper.util.BitmapManager
//import kotlin.math.PI
//import kotlin.math.cos
//import kotlin.math.sin
//import kotlin.random.Random
//
//class Snowflake(private val isSnowfall: Boolean) {
//    var x: Float = Random.nextFloat() * width
//    var y: Float = Random.nextFloat() * height
//    private var angle =
//        (Random.nextFloat() * ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE
//    var velocity = Random.nextFloat(INCREMENT_LOWER, INCREMENT_UPPER)
//    private var radius = setRadius()
//    private var degrees = 0f
//    private var degreeIncrement = 0f // Random.nextFloat(DEGREE_INCREMENT_LOWER, DEGREE_INCREMENT_UPPER)
//    private var texture: Bitmap? = null
//
//    init {
//        log("radius: $radius")
//    }
//
//    fun render(canvas: Canvas) {
//        fall()
//        with(canvas) {
//            if (isSnowfall) drawCircle(x, y, radius, blurPaint)
//            else {
//                texture?.let {
//                    save()
//                    rotate(degrees, x, y)
//                    drawBitmap(it, x, y, bitmapPaint)
//                    restore()
//                    displayText("degrees: $degrees inc: $degreeIncrement", canvas)
//                    displayDebugInfo("$x, $y", canvas, 100f, 350f)
//                } ?: setupTexture()
//            }
//        }
//    }
//
//    private fun fall() {
//        if (isGone()) reset()
//        angle += roll / ANGLE_DIVISOR
//        velocity = if (isSnowfall) pitch * radius * 0.2f else pitch / 2f
//        x += velocity * cos(angle)
//        y += velocity * sin(angle)
//    }
//
//    private fun setupTexture() {
//        texture = BitmapManager.getScaledBitmap(radius)
//    }
//
//    private fun isGone() = x < -radius || x > width + (radius / 2) || y > height + (radius / 2)
//
//    private fun reset() {
//        x = Random.nextFloat() * width
//        y = -1f - radius
//        radius = setRadius()
//        texture = BitmapManager.getScaledBitmap(radius)
//        degrees = 0f
//        degreeIncrement = Random.nextFloat(DEGREE_INCREMENT_LOWER, DEGREE_INCREMENT_UPPER)
//    }
//
//    private fun displayText(text: String, canvas: Canvas) {
//        val bounds = Rect()
//        val textPaint = Paint().apply {
//            textSize = 25f
//            color = Color.WHITE
//            getTextBounds(text, 0, text.length, bounds)
//        }
//        canvas.drawText(text, x, y, textPaint)
//    }
//
//    private fun setRadius() =
//        if (isSnowfall) Random.nextFloat(BACKGROUND_SNOWFLAKE_LOWER, BACKGROUND_SNOWFLAKE_UPPER)
//        else //300f
//            Random.nextInt(0, FLAKES_COUNT_BIG) * 50 + FLAKE_SIZE_LOWER //todo
//
//
//    private fun Random.nextFloat(lower: Float, upper: Float) = nextFloat() * (upper - lower) + lower
//
//    companion object {
//        private const val ANGE_RANGE = 0.2f
//        private const val HALF_ANGLE_RANGE = ANGE_RANGE / 2f
//        private const val HALF_PI = PI.toFloat() / 2f
//        private const val ANGLE_SEED = 25f
//        private const val ANGLE_DIVISOR = 10_000f
//        private const val INCREMENT_LOWER = 2f
//        private const val INCREMENT_UPPER = 4f
//        private const val BACKGROUND_SNOWFLAKE_LOWER = 2f
//        private const val BACKGROUND_SNOWFLAKE_UPPER = 15f
//        private const val DEGREE_INCREMENT_LOWER = 0.0001f
//        private const val DEGREE_INCREMENT_UPPER = 0.0005f
//        private val bitmapPaint = Paint().apply {
//
//        }
//        private val blurPaint = Paint().apply {
//            color = Color.WHITE
//            style = Paint.Style.FILL
//            maskFilter = BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)
//        }
//    }
//}
