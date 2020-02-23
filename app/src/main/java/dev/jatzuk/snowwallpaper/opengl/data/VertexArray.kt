package dev.jatzuk.snowwallpaper.opengl.data

import android.opengl.GLES20.*
import dev.jatzuk.snowwallpaper.opengl.util.BYTES_PER_FLOAT
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VertexArray(vertexData: FloatArray) {
    private val floatBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexData.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(vertexData) }

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
}
