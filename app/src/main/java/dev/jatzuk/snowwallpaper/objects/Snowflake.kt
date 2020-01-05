package dev.jatzuk.snowwallpaper.objects

import dev.jatzuk.snowwallpaper.util.Logger.logging
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Snowflake {
    var x = Random.nextFloat() * 2 - 1f
    var y = Random.nextFloat() + 1f
    private var angle =
        (Random.nextFloat() * ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE
    private var velocity = Random.nextFloat(INCREMENT_LOWER, INCREMENT_UPPER)
//    private var radius = setRadius()
    private var degrees = 0f
    private var degreeIncrement =
        0f // Random.nextFloat(DEGREE_INCREMENT_LOWER, DEGREE_INCREMENT_UPPER)

    fun fall() {
        if (isGone()) reset()
//        angle += roll / ANGLE_DIVISOR
//        velocity = /*if (isSnowfall) */pitch *  0.2f // else pitch / 2f
//        x += velocity * cos(angle)
//        y += velocity * sin(angle)

//        x += 0.001f
//        y -= 0.01f

        val newX = velocity * cos(angle)
        val newY = velocity * sin(angle)

        logging("pos: x: $newX, y: $newY", TAG)

        x += newX
        y -= newY
    }

    private fun isGone() = x < -1f || x > 1f || y < -1f

    private fun reset() {
        x = Random.nextFloat() * 2 - 1f
        y = Random.nextFloat() + 1f
//        radius = setRadius()
        degrees = 0f
//        degreeIncrement = Random.nextFloat(DEGREE_INCREMENT_LOWER, DEGREE_INCREMENT_UPPER)
    }

//    private fun setRadius() =
//        Random.nextFloat(BACKGROUND_SNOWFLAKE_LOWER, BACKGROUND_SNOWFLAKE_UPPER)
//        if (isSnowfall) Random.nextFloat(BACKGROUND_SNOWFLAKE_LOWER, BACKGROUND_SNOWFLAKE_UPPER)
//        else //300f
//            Random.nextInt(0, FLAKES_COUNT_BIG) * 50 + FLAKE_SIZE_LOWER //todo


    private fun Random.nextFloat(lower: Float, upper: Float) = nextFloat() * (upper - lower) + lower

    companion object {
        private val TAG = Snowflake::class.java.simpleName
        private const val ANGE_RANGE = 0.2f
        private const val HALF_ANGLE_RANGE = ANGE_RANGE / 2f
        private const val HALF_PI = PI.toFloat() / 2f
        private const val ANGLE_SEED = 2f//25f
        private const val ANGLE_DIVISOR = 10_000f
        private const val INCREMENT_LOWER = 0.001f//2f
        private const val INCREMENT_UPPER = 0.01f//4f
        private const val BACKGROUND_SNOWFLAKE_LOWER = 2f
        private const val BACKGROUND_SNOWFLAKE_UPPER = 15f
        private const val DEGREE_INCREMENT_LOWER = 0.0001f
        private const val DEGREE_INCREMENT_UPPER = 0.0005f
    }
}
