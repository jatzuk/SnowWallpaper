package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.SnowfallRenderer.Companion.height
import dev.jatzuk.snowwallpaper.opengl.SnowfallRenderer.Companion.width
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService.Companion.roll
import dev.jatzuk.snowwallpaper.utilities.Logger.logging
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

open class Snowflake(context: Context, val isMajorSnowflake: Boolean = false) {

    private val preferenceRepository = PreferenceRepository.getInstance(context)
    var x = getRandomX()
    var y = getRandomY()
    private var isRadiusUnique = preferenceRepository.getIsSnowfallUniqueRadiusEnabled()
    private val minRadius: Float
    private val maxRadius: Float
    var radius: Float
    private var angle = assignDefaultAngle()
    private var velocityFactor = preferenceRepository.getSnowfallVelocityFactor()
    private val incrementLower = velocityFactor.toFloat() * 2
    private val incrementUpper = incrementLower * 2
    private var velocity = getRandomVelocity()
    private var degrees = 0f
    var rotationAxis = getRandomRotationAxis()
//    private var degreeIncrement = Random.nextFloat(DEGREE_INCREMENT_LOWER, DEGREE_INCREMENT_UPPER)

    init {
        if (isRadiusUnique) {
            minRadius = preferenceRepository.getSnowfallMinRadius()
            maxRadius = preferenceRepository.getSnowfallMaxRadius()
        } else {
            minRadius = 8f
            maxRadius = 30f
        }
        radius = getRandomRadius()
        logging("Unique radius set to: $isRadiusUnique", TAG)
    }

    fun fall() {
        if (isOutside()) reset()
        angle += roll
        x += velocity * cos(angle)
        y += velocity * sin(angle)
    }

    private fun isOutside() = x < -radius || x > width + radius || y > height + radius

    private fun reset() {
        x = getRandomX()
        y = getRandomY()
        radius = getRandomRadius()
        angle = assignDefaultAngle()
        velocity = getRandomVelocity()
        degrees = 0f
//        degreeIncrement = Random.nextFloat(DEGREE_INCREMENT_LOWER, DEGREE_INCREMENT_UPPER)
        rotationAxis = getRandomRotationAxis()
    }

    private fun getRandomX() = Random.nextFloat() * width

    private fun getRandomY() = Random.nextFloat() * -radius - radius

    private fun getRandomVelocity(): Float {
        var velocity = Random.nextFloat(incrementLower, incrementUpper)
        if (isMajorSnowflake) velocity /= 2
        return velocity
    }

    private fun getRandomRadius(): Float {
        val radius = if (isRadiusUnique) Random.nextFloat() * maxRadius + minRadius else maxRadius
        return if (isMajorSnowflake) radius * 3 else radius
    }

    private fun assignDefaultAngle() =
        (Random.nextFloat() * ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE

    private fun getRandomRotationAxis(): RotationAxis = RotationAxis.values()[Random.nextInt(2)]

    private fun Random.nextFloat(lower: Float, upper: Float) = nextFloat() * (upper - lower) + lower

    enum class RotationAxis {
        Y, Z
    }

    companion object {
        private val TAG = Snowflake::class.java.simpleName
        private const val ANGE_RANGE = 0.2f
        private const val HALF_ANGLE_RANGE = ANGE_RANGE / 2f
        private const val HALF_PI = PI.toFloat() / 2f
        private const val ANGLE_SEED = 25f
        private const val ANGLE_DIVISOR = 5_000f
        private const val DEGREE_INCREMENT_LOWER = 0.0001f
        private const val DEGREE_INCREMENT_UPPER = 0.0005f
    }
}
