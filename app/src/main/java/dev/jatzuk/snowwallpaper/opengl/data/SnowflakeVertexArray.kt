package dev.jatzuk.snowwallpaper.opengl.data

import dev.jatzuk.snowwallpaper.opengl.objects.Snowflake

class SnowflakeVertexArray(
    private val snowflakes: Array<Snowflake>,
    componentsSize: Int
) : VertexArray(snowflakes.flatMap { listOf(it.x, it.y) }.toFloatArray(), componentsSize) {

    override fun updateBuffer() {
        floatBuffer.position(0)
        for (flake in snowflakes) {
            floatBuffer.put(flake.x)
            floatBuffer.put(flake.y)
        }
    }
}
