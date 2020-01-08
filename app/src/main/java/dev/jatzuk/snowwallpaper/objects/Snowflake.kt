package dev.jatzuk.snowwallpaper.objects

import dev.jatzuk.snowwallpaper.views.MainActivity.Companion.pitch
import dev.jatzuk.snowwallpaper.views.MainActivity.Companion.ratio
import dev.jatzuk.snowwallpaper.views.MainActivity.Companion.roll
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Snowflake {
    var x = (Random.nextFloat() * 2f - ratio) * ratio
    var y = Random.nextFloat() + 1f
    var radius = assignRadius()
    private var angle = assignDefaultAngle()
    private var velocity = Random.nextFloat(INCREMENT_LOWER, INCREMENT_UPPER)
    private var velocityFactor = 2f // todo(load from prefs)
    private var degrees = 0f
//    private var degreeIncrement = Random.nextFloat(DEGREE_INCREMENT_LOWER, DEGREE_INCREMENT_UPPER)

    fun fall() {
        if (isOutside()) reset()
        angle += roll
        velocity += pitch * velocityFactor
        x += velocity * cos(angle)
        y -= velocity * sin(angle)
    }

    private fun isOutside() = x < -1.1f * ratio /** radius / 1000 */|| x > 1f * ratio || y < -1f //todo(apply snowflake radius)

    private fun reset() {
        x = (Random.nextFloat() * 2f - 1f) * ratio
        y = Random.nextFloat() + 1f
        radius = assignRadius()
        angle = assignDefaultAngle() //+ roll * ANGLE_FACTOR
        velocity = Random.nextFloat(INCREMENT_LOWER, INCREMENT_UPPER)
        degrees = 0f
//        degreeIncrement = Random.nextFloat(DEGREE_INCREMENT_LOWER, DEGREE_INCREMENT_UPPER)
    }

    private fun assignRadius() = Random.nextFloat() * 30f + 5f // todo(let user choose min max values)
//        Random.nextFloat(BACKGROUND_SNOWFLAKE_LOWER, BACKGROUND_SNOWFLAKE_UPPER)
//        if (isSnowfall) Random.nextFloat(BACKGROUND_SNOWFLAKE_LOWER, BACKGROUND_SNOWFLAKE_UPPER)
//        else //300f
//            Random.nextInt(0, FLAKES_COUNT_BIG) * 50 + FLAKE_SIZE_LOWER //todo

    private fun assignDefaultAngle() =
        (Random.nextFloat() * ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE

    private fun Random.nextFloat(lower: Float, upper: Float) = nextFloat() * (upper - lower) + lower

    companion object {
        private val TAG = Snowflake::class.java.simpleName
        private const val ANGE_RANGE = 0.2f
        private const val ANGLE_FACTOR = 2f
        private const val HALF_ANGLE_RANGE = ANGE_RANGE / 2f
        private const val HALF_PI = PI.toFloat() / 2f
        private const val ANGLE_SEED = 25f
        private const val ANGLE_DIVISOR = 5_000f
        private val INCREMENT_LOWER = 0.01f * ratio
        private val INCREMENT_UPPER = 0.03f * ratio
        private const val BACKGROUND_SNOWFLAKE_LOWER = 2f
        private const val BACKGROUND_SNOWFLAKE_UPPER = 15f
        private const val DEGREE_INCREMENT_LOWER = 0.0001f
        private const val DEGREE_INCREMENT_UPPER = 0.0005f
    }
}
