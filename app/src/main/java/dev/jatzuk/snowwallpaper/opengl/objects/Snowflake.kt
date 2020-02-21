package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.SnowfallRenderer.Companion.roll
import dev.jatzuk.snowwallpaper.utilities.Logger.logging
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Snowflake(context: Context) {

    private val preferenceRepository = PreferenceRepository.getInstance(context)
    var x = getRandomX()
    var y = getRandomY()
    private var isRadiusUnique = preferenceRepository.getIsSnowfallUniqueRadiusEnabled()
    private val minRadius: Float
    private val maxRadius: Float
    var radius: Float
    private var angle = assignDefaultAngle()
    private var velocityFactor = preferenceRepository.getSnowfallVelocityFactor() * 0.1f
    private val incrementLower = velocityFactor * 0.04f
    private val incrementUpper = incrementLower * 1.5f
    private var velocity = Random.nextFloat(incrementLower, incrementUpper)
    private var degrees = 0f
//    private var degreeIncrement = Random.nextFloat(DEGREE_INCREMENT_LOWER, DEGREE_INCREMENT_UPPER)

    init {
        if (isRadiusUnique) {
            minRadius = preferenceRepository.getSnowfallMinRadius()
            maxRadius = preferenceRepository.getSnowfallMaxRadius()
        } else {
            minRadius = 8f
            maxRadius = 30f
        }
        logging("Unique radius set to: $isRadiusUnique", TAG)
        radius = getRandomRadius()
    }

    fun fall() {
        if (isOutside()) reset()
        angle += roll
        x += velocity * cos(angle)
        y -= velocity * sin(angle)
//      logging("$x, $y", TAG)
    }

    private fun isOutside() = x < -1 || x > 1 || y < -1

    private fun reset() {
        x = getRandomX()
        y = getRandomY()
        radius = getRandomRadius()
        angle = assignDefaultAngle()
        velocity = Random.nextFloat(incrementLower, incrementUpper)
        degrees = 0f
//        degreeIncrement = Random.nextFloat(DEGREE_INCREMENT_LOWER, DEGREE_INCREMENT_UPPER)
    }

    private fun getRandomX() = Random.nextFloat() * 2 - 1

    private fun getRandomY() = Random.nextFloat() + 1

    private fun getRandomRadius() =
        if (isRadiusUnique) Random.nextFloat() * maxRadius + minRadius else maxRadius
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
        private const val HALF_ANGLE_RANGE = ANGE_RANGE / 2f
        private const val HALF_PI = PI.toFloat() / 2f
        private const val ANGLE_SEED = 25f
        private const val ANGLE_DIVISOR = 5_000f
        private const val BACKGROUND_SNOWFLAKE_LOWER = 2f
        private const val BACKGROUND_SNOWFLAKE_UPPER = 15f
        private const val DEGREE_INCREMENT_LOWER = 0.0001f
        private const val DEGREE_INCREMENT_UPPER = 0.0005f
    }
}
