package com.jupiter.goblin.util

import com.badlogic.gdx.math.Vector2

/*
 * Copyright (c) 2015 Nathan S. Templon
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
private val _toDegrees = 180.0 / Math.PI
private val _toRadians = Math.PI / 180.0
private val _toDegreesFloat = 180.0f / Math.PI.toFloat()
private val _toRadiansFloat = Math.PI.toFloat() / 180.0f

fun Double.toDegrees(): Double = this * _toDegrees
fun Double.toRadians(): Double = this * _toRadians
fun Float.toDegrees(): Float = this * _toDegreesFloat
fun Float.toRadians(): Float = this * _toRadiansFloat

fun Double.sqrt(): Double = Math.sqrt(this)
fun Float.sqrt(): Float = Math.sqrt(this.toDouble()).toFloat()

fun Double.abs(): Double = Math.abs(this)
fun Float.abs(): Float = Math.abs(this)

operator fun Vector2.plus(other: Vector2): Vector2 = this.cpy().add(other)
operator fun Vector2.minus(other: Vector2): Vector2 = this.cpy().sub(other)
operator fun Vector2.times(scalar: Float): Vector2 = Vector2(this.x * scalar, this.y * scalar)
operator fun Vector2.div(scalar: Float): Vector2 = Vector2(this.x / scalar, this.y / scalar)

fun Vector2.normalized(): Vector2 = this.cpy().nor()
fun Vector2.copy(): Vector2 = this.cpy()

val Vector2.length: Float
    get() = this.len()

/**
 * Rotates the vector by the provided angle
 *
 * @param angle the angle to rotate by, in radians (positive counter-clockwise)
 */
fun Vector2.revolve(angle: Float): Vector2 {
    val sin = Math.sin(angle.toDouble())
    val cos = Math.cos(angle.toDouble())

    val x = this.x * cos - this.y * sin
    val y = this.x * sin + this.y * cos

    return Vector2(x.toFloat(), y.toFloat())
}