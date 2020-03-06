package dev.jatzuk.snowwallpaper.opengl.data

import dev.jatzuk.snowwallpaper.opengl.SnowfallRenderer.Companion.height
import dev.jatzuk.snowwallpaper.opengl.SnowfallRenderer.Companion.width
import dev.jatzuk.snowwallpaper.opengl.objects.Snowflake
import dev.jatzuk.snowwallpaper.opengl.wallpaper.OpenGLWallpaperService.Companion.ratio

class RectangleVertexArray(
    private val snowflakes: Array<Snowflake>,
    componentsSize: Int
) : VertexArray(snowflakes.flatMap { listOf(it.x, it.y) }.toFloatArray(), componentsSize) {

    init {
        overlayTexture()
    }

    override fun updateBuffer() {
        floatBuffer.position(0)
        for (flake in snowflakes) {
            val areaFactor = flake.radius * ratio
            if (height > width) {
                floatBuffer.put(flake.x - flake.radius)
                floatBuffer.put(flake.y + areaFactor)
                floatBuffer.shiftPositionOnTo(3)

                floatBuffer.put(flake.x - flake.radius)
                floatBuffer.put(flake.y - areaFactor)
                floatBuffer.shiftPositionOnTo(3)

                floatBuffer.put(flake.x + flake.radius)
                floatBuffer.put(flake.y + areaFactor)
                floatBuffer.shiftPositionOnTo(3)

                floatBuffer.put(flake.x + flake.radius)
                floatBuffer.put(flake.y - areaFactor)
                floatBuffer.shiftPositionOnTo(3)

            } else {
                floatBuffer.put(flake.x - areaFactor)
                floatBuffer.put(flake.y + flake.radius)
                floatBuffer.shiftPositionOnTo(3)

                floatBuffer.put(flake.x - areaFactor)
                floatBuffer.put(flake.y - flake.radius)
                floatBuffer.shiftPositionOnTo(3)

                floatBuffer.put(flake.x + areaFactor)
                floatBuffer.put(flake.y + flake.radius)
                floatBuffer.shiftPositionOnTo(3)

                floatBuffer.put(flake.x + areaFactor)
                floatBuffer.put(flake.y - flake.radius)
                floatBuffer.shiftPositionOnTo(3)
            }
        }
    }

    private fun overlayTexture() {
        floatBuffer.position(0)
        for (flake in snowflakes) {
            floatBuffer.shiftPositionOnTo(2)
            floatBuffer.put(1f)
            floatBuffer.put(0f)
            floatBuffer.put(0f)

            floatBuffer.shiftPositionOnTo(2)
            floatBuffer.put(1f)
            floatBuffer.put(0f)
            floatBuffer.put(1f)

            floatBuffer.shiftPositionOnTo(2)
            floatBuffer.put(1f)
            floatBuffer.put(1f)
            floatBuffer.put(0f)

            floatBuffer.shiftPositionOnTo(2)
            floatBuffer.put(1f)
            floatBuffer.put(1f)
            floatBuffer.put(1f)
        }
    }
}
