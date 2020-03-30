package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService.Companion.height
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService.Companion.roll
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService.Companion.width
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

open class Snowflake(context: Context, val isMajorSnowflake: Boolean = false) {

    var x: Float
    var y: Float
    private var isRadiusUnique: Boolean
    private val minRadius: Int
    private val maxRadius: Int
    var radius: Int
    private var velocityFactor: Int
    private var velocity: Int
    private var angle = assignDefaultAngle()
    var rotationAxis = getRandomRotationAxis()
    private val rotationIncrement: Float

    private var degreeIncrement: Float
    var rotationDegrees: Float

    private val preferenceRepository = PreferenceRepository.getInstance(context)

    private var deviation = preferenceRepository.getCosineDeviation()

    init {
        if (isMajorSnowflake) {
            velocityFactor = preferenceRepository.getSnowflakeVelocityFactor()
            isRadiusUnique = preferenceRepository.getIsSnowflakeUniqueRadiusEnabled()
            if (isRadiusUnique) {
                minRadius = preferenceRepository.getSnowflakeMinRadius()
                maxRadius = preferenceRepository.getSnowflakeMaxRadius()
            } else {
                minRadius = preferenceRepository.getSnowflakeRadiusWhenUniqueDisabled()
                maxRadius = minRadius
            }
        } else {
            velocityFactor = preferenceRepository.getSnowfallVelocityFactor() * 2
            isRadiusUnique = preferenceRepository.getIsSnowfallUniqueRadiusEnabled()
            if (isRadiusUnique) {
                minRadius = preferenceRepository.getSnowfallMinRadius()
                maxRadius = preferenceRepository.getSnowfallMaxRadius()
            } else {
                minRadius = preferenceRepository.getSnowfallRadiusWhenUniqueDisabled()
                maxRadius = minRadius
            }
        }


        velocity = getRandomVelocity()

        radius = getRandomRadius()

        x = getRandomX()
        y = getRandomY()

        rotationIncrement =
            preferenceRepository.getSnowflakeRotationVelocity().toFloat() / 2
        degreeIncrement = Random.nextFloat(rotationIncrement, rotationIncrement * 2)
        rotationDegrees = Random.nextFloat(0f, rotationIncrement)
    }

    fun fall() {
        if (isOutside()) reset()
        rotationDegrees += degreeIncrement
        angle += roll
        x += velocity * cos(angle) * deviation
        y += velocity * sin(angle)
    }

    private fun isOutside() = x < -radius || x > width + radius || y > height + radius

    private fun reset() {
        velocity = getRandomVelocity()
        radius = getRandomRadius()
        x = getRandomX()
        y = getRandomY()
        angle = assignDefaultAngle()
        rotationAxis = getRandomRotationAxis()
        rotationDegrees = 0f
        degreeIncrement = getRandomDegreeIncrement()
    }

    private fun getRandomX(): Float = Random.nextInt(width).toFloat()

    private fun getRandomY(): Float = -Random.nextInt(radius * 2, height / 2).toFloat()

    private fun getRandomVelocity(): Int = Random.nextInt(velocityFactor, velocityFactor * 2 + 1)

    private fun getRandomRadius(): Int = Random.nextInt(minRadius, maxRadius + 1)

    private fun assignDefaultAngle() =
        (Random.nextFloat() * ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE

    private fun getRandomRotationAxis(): RotationAxis = RotationAxis.values()[Random.nextInt(2)]

    private fun getRandomDegreeIncrement(): Float =
        Random.nextFloat(rotationIncrement / 2f, rotationIncrement * 2)

    private fun Random.nextFloat(lower: Float, upper: Float) = nextFloat() * (upper - lower) + lower

    enum class RotationAxis {
        Y, Z
    }

    companion object {
        private const val TAG = "Snowflake"
        private const val ANGE_RANGE = 0.2f
        private const val HALF_ANGLE_RANGE = ANGE_RANGE / 2f
        private const val HALF_PI = PI.toFloat() / 2f
        private const val ANGLE_SEED = 25f
        private const val ANGLE_DIVISOR = 5_000f
    }
}
