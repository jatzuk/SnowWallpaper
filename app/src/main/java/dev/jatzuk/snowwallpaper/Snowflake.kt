package dev.jatzuk.snowwallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import dev.jatzuk.snowwallpaper.WallpaperServiceImpl.Companion.height
import dev.jatzuk.snowwallpaper.WallpaperServiceImpl.Companion.width
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Snowflake {
    var x: Float = Random.nextFloat() * width
    var y: Float = Random.nextFloat() * height
    private var angle =
        (Random.nextFloat() * ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE
    var velocity = Random.nextFloat(INCREMENT_LOWER, INCREMENT_UPPER)
    var acceleration = 0f
    private val radius = Random.nextFloat(FLAKE_SIZE_LOWER, FLAKE_SIZE_UPPER)

    fun draw(canvas: Canvas) {
        fall()
        canvas.drawCircle(x, y, radius, paint)
    }

    private fun fall() {
        if (isGone()) reset()
        x += velocity * cos(angle)
        y += velocity * sin(angle) // SensorManager.STANDARD_GRAVITY
        angle += Random.nextFloat(-ANGLE_SEED, ANGLE_SEED) / ANGLE_DIVISOR
    }

    private fun isGone() = x < -radius || x > width + radius || y > height + radius

    private fun reset() {
        x = Random.nextFloat() * width
        y = -1f - radius
//        angle = 0f
//        velocity = 0f
//        acceleration = 0f
    }

    private fun Random.nextFloat(lower: Float, upper: Float) = nextFloat() * (upper - lower) + lower

    companion object {
        private const val ANGE_RANGE = 0.2f
        private const val HALF_ANGLE_RANGE = ANGE_RANGE / 2f
        private const val HALF_PI = PI.toFloat() / 2f
        private const val ANGLE_SEED = 25f
        private const val ANGLE_DIVISOR = 10_000f
        private const val INCREMENT_LOWER = 2f
        private const val INCREMENT_UPPER = 4f
        private const val FLAKE_SIZE_LOWER = 7f
        private const val FLAKE_SIZE_UPPER = 20f
        private val paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.FILL
        }
    }
}
