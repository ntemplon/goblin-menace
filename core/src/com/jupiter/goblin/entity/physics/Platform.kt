package com.jupiter.goblin.entity.physics

import com.jupiter.goblin.util.Vec2

/**
 * Copyright (c) 2016 Nathan S. Templon
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

/**
 * A class representing a platform / continuous string of terrain
 */
class Platform(val vertices: List<Vec2>) : PhysicsRenderer.PhysicsRenderable {

    override val physicsVertices: FloatArray = this.vertices.flatMap { listOf(it.x, it.y) }.toFloatArray()
    val segments: List<Segment> = this.vertices.withIndex().map {
        if (it.index < this.vertices.size - 1) {
            Segment(it.value, this.vertices[it.index + 1])
        } else {
            null
        }
    }.filterNotNull()

    class Segment(val start: Vec2, val end: Vec2) {
        val slope: Float = (this.end.y - this.start.y) / (this.end.x - this.start.x)
        val isVertical: Boolean = this.slope.isNaN()
    }

}