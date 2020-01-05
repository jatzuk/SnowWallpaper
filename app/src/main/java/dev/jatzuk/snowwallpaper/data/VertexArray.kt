package dev.jatzuk.snowwallpaper.data

import android.opengl.GLES20.*
import dev.jatzuk.snowwallpaper.objects.SnowfallBackground.Companion.POSITION_COMPONENT_COUNT
import dev.jatzuk.snowwallpaper.objects.Snowflake
import dev.jatzuk.snowwallpaper.util.BYTES_PER_FLOAT
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VertexArray(private val snowflakes: Array<Snowflake>) {
    private val floatBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(snowflakes.size * POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

    fun setVertexAttribPointer(
        dataOffset: Int,
        attributeLocation: Int,
        componentCount: Int,
        stride: Int
    ) {
        floatBuffer.position(dataOffset)
        glVertexAttribPointer(
            attributeLocation,
            componentCount,
            GL_FLOAT,
            false,
            stride,
            floatBuffer
        )
        glEnableVertexAttribArray(attributeLocation)
    }

    fun updateBuffer() {
        floatBuffer.position(0)
        for (flake in snowflakes) {
            floatBuffer.put(flake.x)
            floatBuffer.put(flake.y)
        }
    }
}
