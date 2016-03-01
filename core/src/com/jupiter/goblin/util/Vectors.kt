package com.jupiter.goblin.util

import com.badlogic.gdx.math.Vector2

/*
 * Copyright (c) 2016 Nathan S. Templon
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
data class Vec2(val x: Float, val y: Float) {

    // Initialization
    constructor() : this(0f, 0f)


    // Properties
    val norm2: Float = this.x * this.x + this.y * this.y
    val norm: Float by lazy { this.norm2.sqrt() }
    val direction: Vec2 by lazy { this / this.norm }


    // Operators
    operator fun plus(other: Vec2): Vec2 = Vec2(this.x + other.x, this.y + other.y)

    operator fun minus(other: Vec2): Vec2 = Vec2(this.x - other.x, this.y - other.y)
    operator fun times(scalar: Float): Vec2 = Vec2(this.x * scalar, this.y * scalar)
    operator fun div(scalar: Float): Vec2 = this * (1f / scalar)

    operator fun times(scalar: Double): Vec2 = this * scalar.toFloat()
    operator fun times(scalar: Int): Vec2 = this * scalar.toFloat()
    operator fun div(scalar: Double): Vec2 = this / scalar.toFloat()
    operator fun div(scalar: Int): Vec2 = this / scalar.toFloat()


    // Public Methods
    fun dot(other: Vec2): Float = this.x * other.x + this.y * other.y

    fun distance2(other: Vec2): Float {
        val dx = this.x - other.x
        val dy = this.y - other.y
        return dx * dx + dy * dy
    }

    fun distance(other: Vec2): Float = this.distance2(other).sqrt()

    /**
     * Rotates the vector by the provided angle
     *
     * @param angle the angle to rotate by, in radians (positive counter-clockwise)
     */
    fun rotate(angle: Float): Vec2 {
        val sin = Math.sin(angle.toDouble())
        val cos = Math.cos(angle.toDouble())

        val x = this.x * cos - this.y * sin
        val y = this.x * sin + this.y * cos

        return Vec2(x.toFloat(), y.toFloat())
    }

    fun asGdxVector(): Vector2 = Vector2(this.x, this.y)


    companion object {
        operator fun Float.times(vector: Vec2): Vec2 = vector * this
        operator fun Double.times(vector: Vec2): Vec2 = vector * this
        operator fun Int.times(vector: Vec2): Vec2 = vector * this
    }

}