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
    private val velocityLowerIncrement: Int
    private val velocityUpperIncrement: Int
    private var velocity: Int
    private var angle = assignDefaultAngle()
    var rotationAxis = getRandomRotationAxis()
    private val rotationDegreeLowerIncrement: Float
    private val rotationDegreeUpperIncrement: Float
    private var degreeIncrement: Float
    var rotationDegrees: Float

    private val preferenceRepository = PreferenceRepository.getInstance(context)

    init {
//           logging("Unique radius set to: $isRadiusUnique", TAG)

        if (isMajorSnowflake) {
            velocityFactor = preferenceRepository.getSnowflakeVelocityFactor()
            isRadiusUnique = preferenceRepository.getIsSnowflakeUniqueRadiusEnabled()
            if (isRadiusUnique) {
                minRadius = preferenceRepository.getSnowflakeMinRadius()
                maxRadius = preferenceRepository.getSnowflakeMaxRadius()
            } else {
                minRadius = 50
                maxRadius = 100
            }
        } else {
            velocityFactor = preferenceRepository.getSnowfallVelocityFactor()
            isRadiusUnique = preferenceRepository.getIsSnowfallUniqueRadiusEnabled()
            if (isRadiusUnique) {
                minRadius = preferenceRepository.getSnowfallMinRadius()
                maxRadius = preferenceRepository.getSnowfallMaxRadius()
            } else {
                minRadius = 8
                maxRadius = 30
            }
        }

        velocityLowerIncrement = velocityFactor * 2
        velocityUpperIncrement = velocityLowerIncrement * 2
        velocity = getRandomVelocity()
        radius = getRandomRadius()
        x = getRandomX()
        y = getRandomY()

        rotationDegreeLowerIncrement =
            preferenceRepository.getSnowflakeRotationVelocity().toFloat()
        rotationDegreeUpperIncrement = rotationDegreeLowerIncrement * 1.1f
        degreeIncrement =
            Random.nextFloat(rotationDegreeLowerIncrement, rotationDegreeUpperIncrement)
        rotationDegrees =
            Random.nextFloat(rotationDegreeLowerIncrement, rotationDegreeUpperIncrement)
    }

    fun fall() {
        if (isOutside()) reset()
        rotationDegrees += degreeIncrement
        angle += roll
        x += velocity * cos(angle)
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
        degreeIncrement =
            Random.nextFloat(rotationDegreeLowerIncrement, rotationDegreeUpperIncrement)
    }

    private fun getRandomX(): Float = Random.nextInt(width).toFloat()

    private fun getRandomY(): Float = -Random.nextInt(1, radius * 2).toFloat()

    private fun getRandomVelocity(): Int =
        Random.nextInt(velocityLowerIncrement, velocityUpperIncrement)

    private fun getRandomRadius(): Int = Random.nextInt(minRadius, maxRadius + 1)

    private fun assignDefaultAngle() =
        (Random.nextFloat() * ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE

    private fun getRandomRotationAxis(): RotationAxis = RotationAxis.values()[Random.nextInt(2)]

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
        private const val DEGREE_INCREMENT_LOWER = 0.0001f
        private const val DEGREE_INCREMENT_UPPER = 0.0005f
    }
}
