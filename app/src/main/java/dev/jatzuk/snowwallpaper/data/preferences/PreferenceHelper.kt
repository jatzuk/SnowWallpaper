package dev.jatzuk.snowwallpaper.data.preferences

interface PreferenceHelper {
//    background flake enabled
//    flakes limit
//    vel factor
//    unique radius
//    min radius
//    max radius

//    background enabled

    fun isSnowfallEnabled(): Boolean

    fun getSnowfallLimit(): Int

    fun getVelocityFactor(): Int

    fun isUniqueRadiusEnabled(): Boolean

    fun getMinRadius(): Int

    fun getMaxRadius(): Int

    fun isBackgroundImageEnabled(): Boolean

    fun setBackgroundImageEnabled(isEnabled: Boolean)
}
