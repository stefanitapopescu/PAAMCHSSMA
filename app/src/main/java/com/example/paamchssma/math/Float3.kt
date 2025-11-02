package com.example.paamchssma.math

/**
 * Data class simplu pentru coordonate 3D (x, y, z)
 * Folosit pentru poziții în spațiul 3D
 */
data class Float3(
    val x: Float,
    val y: Float,
    val z: Float
) {
    constructor(value: Float) : this(value, value, value)
    
    operator fun plus(other: Float3) = Float3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Float3) = Float3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Float3(x * scalar, y * scalar, z * scalar)
    operator fun div(scalar: Float) = Float3(x / scalar, y / scalar, z / scalar)
    
    companion object {
        val ZERO = Float3(0f, 0f, 0f)
        val ONE = Float3(1f, 1f, 1f)
        val UP = Float3(0f, 1f, 0f)
        val DOWN = Float3(0f, -1f, 0f)
        val FORWARD = Float3(0f, 0f, 1f)
        val BACK = Float3(0f, 0f, -1f)
        val RIGHT = Float3(1f, 0f, 0f)
        val LEFT = Float3(-1f, 0f, 0f)
    }
}

