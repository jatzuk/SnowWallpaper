package dev.jatzuk.snowwallpaper.objects

import android.content.Context
import androidx.preference.PreferenceManager
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.util.Logger.logging
import dev.jatzuk.snowwallpaper.views.MainActivity.Companion.ratio
import dev.jatzuk.snowwallpaper.views.MainActivity.Companion.roll
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Snowflake(context: Context) {
    var x = (Random.nextFloat() * 2f - ratio) * ratio
    var y = Random.nextFloat() + 1f
    private var isRadiusUnique = PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(context.getString(R.string.background_snowflakes_random_radius_key), true)
    private val minRadius: Float
    private val maxRadius: Float
    var radius: Float
    private var angle = assignDefaultAngle()
    private var velocityFactor = PreferenceManager.getDefaultSharedPreferences(context)
        .getInt(context.getString(R.string.background_snowflakes_velocity_factor_key), 2) * 0.1f
    private val incrementLower = velocityFactor * 0.04f
    private val incrementUpper = incrementLower * 1.5f
    private var velocity = Random.nextFloat(incrementLower, incrementUpper)
    private var degrees = 0f
//    private var degreeIncrement = Random.nextFloat(DEGREE_INCREMENT_LOWER, DEGREE_INCREMENT_UPPER)

    init {
        if (isRadiusUnique) {
            minRadius = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(context.getString(R.string.background_snowflakes_min_radius_key), 5)
                .toFloat()
            maxRadius = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(context.getString(R.string.background_snowflakes_max_radius_key), 30)
                .toFloat()
        } else {
            minRadius = 1f
            maxRadius = 30f
        }
        logging("Unique radius set to: $isRadiusUnique", TAG)
        radius = assignRadius()
    }

    fun fall() {
        if (isOutside()) reset()
        angle += roll
        x += velocity * cos(angle)
        y -= velocity * sin(angle)
    }

    private fun isOutside() =
        x < -1.1f * ratio - (radius / 2) || x > 1.1f * ratio + (radius / 2) || y < -1f

    private fun reset() {
        x = (Random.nextFloat() * 2f - 1f) * ratio
        y = Random.nextFloat() + 1f
        radius = assignRadius()
        angle = assignDefaultAngle()
        velocity = Random.nextFloat(incrementLower, incrementUpper)
        degrees = 0f
//        degreeIncrement = Random.nextFloat(DEGREE_INCREMENT_LOWER, DEGREE_INCREMENT_UPPER)
    }

    private fun assignRadius() =
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
