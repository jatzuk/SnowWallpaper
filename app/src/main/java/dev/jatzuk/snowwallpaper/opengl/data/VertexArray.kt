package dev.jatzuk.snowwallpaper.opengl.data

import android.opengl.GLES20.*
import dev.jatzuk.snowwallpaper.opengl.util.BYTES_PER_FLOAT
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

open class VertexArray(vertexData: FloatArray, componentsSize: Int) {
    protected val floatBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexData.size / 2 * componentsSize * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(vertexData) }

    fun setVertexAttribPointer(
        dataOffset: Int,
        attributeLocation: Int,
        componentCount: Int,
        stride: Int,
        normalized: Boolean = false
    ) {
        floatBuffer.position(dataOffset)
        glVertexAttribPointer(
            attributeLocation,
            componentCount,
            GL_FLOAT,
            normalized,
            stride,
            floatBuffer
        )
        glEnableVertexAttribArray(attributeLocation)
    }

    open fun updateBuffer() {
        floatBuffer.clear()
    }

    protected fun FloatBuffer.shiftPositionOnTo(position: Int) {
        position(position() + position)
    }
}
