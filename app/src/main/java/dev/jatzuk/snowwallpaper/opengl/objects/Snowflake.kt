package dev.jatzuk.snowwallpaper.opengl.objects

import android.content.Context
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService.Companion.height
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService.Companion.pitch
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService.Companion.roll
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService.Companion.width
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Snowflake(context: Context, private val isTexturedSnowflake: Boolean = false) {

    var x = 0f
    var y = 0f
    var z = 0f
    private var isRadiusUnique = false
    private var minRadius = 0
    private var maxRadius = 0
    var radius = 0
    private var velocityFactor = 0
    private var velocity = 0
    private var angle = assignDefaultAngle()
    var shouldRotate = false
    private var availableRotationAxes = ArrayList<RotationAxis>()
    var rotationAxis = RotationAxis.NONE
    private var rotationIncrement = 0f

    private var degreeIncrement = 0f
    var rotationDegrees = 0f

    private val preferenceRepository = PreferenceRepository.getInstance(context)

    private var deviation = 1

    init {
        updatePreferenceConstraints()
        updatePosition()
    }

    private fun updateRadiusConstraints() {
        if (isTexturedSnowflake) {
            isRadiusUnique = preferenceRepository.getIsSnowflakeUniqueRadiusEnabled()
            if (isRadiusUnique) {
                minRadius = preferenceRepository.getSnowflakeMinRadius()
                maxRadius = preferenceRepository.getSnowflakeMaxRadius()
            } else {
                minRadius = preferenceRepository.getSnowflakeRadiusWhenUniqueDisabled()
                maxRadius = minRadius
            }
        } else {
            isRadiusUnique = preferenceRepository.getIsSnowfallUniqueRadiusEnabled()
            if (isRadiusUnique) {
                minRadius = preferenceRepository.getSnowfallMinRadius()
                maxRadius = preferenceRepository.getSnowfallMaxRadius()
            } else {
                minRadius = preferenceRepository.getSnowfallRadiusWhenUniqueDisabled()
                maxRadius = minRadius
            }
        }
    }

    fun fall() {
        if (isOutside()) reset()
        rotationDegrees += degreeIncrement
        angle += roll
        x += velocity * cos(angle) * deviation
        y += velocity * sin(angle)

        if (isTexturedSnowflake) {
            if (pitch > 0 && z < radius * 2) z += pitch
            else if (pitch < 0 && z > -radius / 2) z += pitch
        }
    }

    private fun isOutside() = x < -radius || x > width + radius || y > height + radius

    private fun reset() {
        velocity = getRandomVelocity()
        radius = getRandomRadius()
        updatePosition()
        angle = assignDefaultAngle()
        rotationAxis = getRandomRotationAxis()
        rotationDegrees = 0f
        degreeIncrement = getRandomDegreeIncrement()
    }

    private fun updatePosition() {
        x = getRandomX()
        y = getRandomY()
        z = 0f
    }

    fun updatePreferenceConstraints() {
        updateRadius()
        updateVelocity()
        if (isTexturedSnowflake) updateRotationAxis()
        deviation = preferenceRepository.getCosineDeviation()
    }

    private fun updateRadius() {
        updateRadiusConstraints()
        radius = getRandomRadius()
    }

    private fun updateVelocity() {
        updateVelocityFactor()
        velocity = getRandomVelocity()
    }

    private fun updateRotationAxis() {
        checkAvailableRotationAxes()
        shouldRotate = availableRotationAxes.isNotEmpty()
        rotationAxis = getRandomRotationAxis()
        rotationIncrement = preferenceRepository.getSnowflakeRotationVelocity().toFloat() / 2f
        degreeIncrement = Random.nextFloat(rotationIncrement, rotationIncrement * 2)
        rotationDegrees = Random.nextFloat(0f, rotationIncrement)
    }

    private fun getRandomX(): Float = Random.nextInt(width).toFloat()

    private fun getRandomY(): Float = -Random.nextInt(radius * 2, height / 2).toFloat()

    private fun getRandomVelocity(): Int = Random.nextInt(velocityFactor, velocityFactor * 2 + 1)

    private fun getRandomRadius(): Int = Random.nextInt(minRadius, maxRadius + 1)

    private fun assignDefaultAngle() =
        (Random.nextFloat() * ANGLE_SEED) / ANGLE_SEED * ENGAGE_RANGE + HALF_PI - HALF_ANGLE_RANGE

    private fun getRandomRotationAxis(): RotationAxis =
        try {
            availableRotationAxes[Random.nextInt(availableRotationAxes.size)]
        } catch (e: IllegalArgumentException) {
            RotationAxis.NONE
        }

    private fun updateVelocityFactor() {
        velocityFactor =
            if (isTexturedSnowflake) preferenceRepository.getSnowflakeVelocityFactor()
            else preferenceRepository.getSnowfallVelocityFactor() * 2
    }

    private fun checkAvailableRotationAxes() {
        val availableRotationAxes = preferenceRepository.getSnowflakeAvailableRotationAxes()
        val (isRotatesX, isRotatesY, isRotatesZ) = availableRotationAxes
        if (!isRotatesX && !isRotatesY && !isRotatesZ) rotationAxis = RotationAxis.NONE
        else {
            if (isRotatesX) this.availableRotationAxes.add(RotationAxis.X)
            if (isRotatesY) this.availableRotationAxes.add(RotationAxis.Y)
            if (isRotatesZ) this.availableRotationAxes.add(RotationAxis.Z)
        }
    }

    private fun getRandomDegreeIncrement(): Float =
        Random.nextFloat(rotationIncrement / 2f, rotationIncrement * 2)

    private fun Random.nextFloat(lower: Float, upper: Float) = nextFloat() * (upper - lower) + lower

    enum class RotationAxis {
        X, Y, Z, NONE
    }

    companion object {
        private const val TAG = "Snowflake"
        private const val ENGAGE_RANGE = 0.2f
        private const val HALF_ANGLE_RANGE = ENGAGE_RANGE / 2f
        private const val HALF_PI = PI.toFloat() / 2f
        private const val ANGLE_SEED = 25f
    }
}
