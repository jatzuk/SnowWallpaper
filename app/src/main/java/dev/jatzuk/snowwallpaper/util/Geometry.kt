package dev.jatzuk.snowwallpaper.util

import kotlin.math.sqrt

sealed class Geometry {

    class Point(var x: Float, var y: Float, var z: Float) {
        fun translateY(distance: Float) = Point(x, y + distance, z)

        fun translate(vector: Vector) = Point(x + vector.x, y + vector.y, z + vector.z)
    }

    class Vector(var x: Float, var y: Float, var z: Float) {
        val length = sqrt(x * x + y * y + z * z)

        fun crossProduct(other: Vector) = Vector(
            (y * other.z) - (z * other.y),
            (z * other.x) - (x * other.z),
            (x * other.y) - (y * other.x)
        )

        fun dotProduct(other: Vector): Float = x * other.x + y * other.y + z * other.z

        fun scale(float: Float) = Vector(x * float, y * float, z * float)
    }
}
