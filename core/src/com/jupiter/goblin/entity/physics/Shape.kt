package com.jupiter.goblin.entity.physics

import com.jupiter.goblin.util.Vec2
import com.jupiter.goblin.util.sqrt

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
sealed class Shape(position: Vec2) {

    var position: Vec2 = position
        get() = field
        set(value) {
            field = value
            this.onPositionChanged()
        }

    abstract val maxRadius: Float


    fun circleIntersects(other: Shape): Boolean {
        val combinedDistance = this.maxRadius + other.maxRadius
        return this.position.distance2(other.position) <= (combinedDistance * combinedDistance)
    }

    open fun onPositionChanged() {
    }

    abstract fun isCollidingWith(other: Shape): Boolean


    /**
     * A shape representing a polygon
     * @param position the position of the polygon
     * @param verts the vertices of the polygon, in body coordinates. It is assumed that they form a convex
     * polygon, and that they are in counter-clockwise order (the right side of each edge is the exterior).
     *
     * @property vertices the vertices of the polygon, in body coordinates, counter-clockwise from the starting
     * vertex.
     */
    class Polygon(position: Vec2, verts: List<Vec2>) : Shape(position) {

        init {
            if (verts.size < 3) {
                throw IllegalArgumentException("The number of vertices must be at least 3!")
            }
        }

        val vertices: List<Vec2>

        init {
            // Calculate the first vertex (it is the one with the highest x coordinate, or y in the case of a tie
            val firstVertex = verts
                    .withIndex()
                    .reduce { first, second ->
                        if (first.value.x > second.value.x) {
                            first
                        } else if (first.value.x < second.value.x) {
                            second
                        } else {
                            if (first.value.y > second.value.y) {
                                first
                            } else {
                                second
                            }
                        }
                    }.index

            this.vertices = Array(verts.size, { i ->
                verts[(i + firstVertex) % verts.size]
            }).toList()
        }

        var worldVertices: List<Vec2> = listOf()
            private set

        init {
            this.computeWorldVertices()
        }

        override val maxRadius: Float = (vertices.map { it.distance2(position) }.max() ?: 0f).sqrt()


        override fun onPositionChanged() {
            this.computeWorldVertices()
        }

        override fun isCollidingWith(other: Shape): Boolean {
            return when (other) {
                is Polygon -> polygonCollision(other)
            }
        }

        fun support(direction: Vec2): Vec2 {
            return this.vertices[this.worldSupportWithIndex(direction).index]
        }

        fun worldSupportWithIndex(direction: Vec2): IndexedValue<Vec2> {
            // Assures us that the !! operator below will never fail
            assert(this.vertices.size > 0)

            return this.worldVertices.withIndex().maxBy { it.value.dot(direction) }!!
        }

        fun worldSupport(direction: Vec2): Vec2 {
            // Assures us that the !! operator below will never fail
            assert(this.vertices.size > 0)

            return this.worldVertices.maxBy { it.dot(direction) }!!
        }

        fun toWorldCoordinates(bodyCoords: Vec2): Vec2 = bodyCoords + this.position

        private fun polygonCollision(other: Polygon): Boolean {
            val a = this
            val b = other

            // Theorem:  s(Minkowski Difference)(d) = sa(d) - sb(-d)
            fun mdSupport(direction: Vec2): Vec2 = a.worldSupport(direction) - b.worldSupport(direction * -1f)

            // Take an initial guess at 'd': We use the direction vector from A to B
            val simplex = Simplex()
            var d = b.position - a.position

            simplex.add(mdSupport(d))
            d *= -1f

            var finished = false
            var colliding = false
            // Time to loop
            while (!finished) {
                // Add a new point to the simplex
                simplex.add(mdSupport(d))
                simplex.updateState()

                // Make sure that the last point we added passed the origin
                if (simplex.a!!.dot(d) <= 0) {
                    // If the point added last was not past the origin in the direction of d,
                    //   then the Minkowski Difference cannot contain the origin since the
                    //   last point added is on the edge of the Minkowsi Difference
                    colliding = false
                    finished = true
                } else {
                    // Determine if the origin is in the current simplex
                    if (simplex.containsOrigin) {
                        // Simplex contains origin, and there is a collision
                        colliding = true
                        finished = true
                    } else {
                        // We cannot be certain if the shapes are colliding or not, so we find the edge who is closest
                        //   to the origin and use its normal (in the direction of the origin) as the new d and loop
                        //   again
                        d = simplex.nextDirection
                    }
                }
            }

            return colliding
        }

        private fun computeWorldVertices() {
            this.worldVertices = this.vertices.map { this.toWorldCoordinates(it) }
        }

        companion object {
            // 90 degrees clockwise
            private val EDGE_TO_NORMAL_ANGLE: Float = (Math.PI * -0.5).toFloat()
        }

    }

}

private class Simplex() {

    // Properties
    var a: Vec2? = null
        private set
    var b: Vec2? = null
        private set
    var c: Vec2? = null
        private set

    var count: Int = 0
        private set(value) {
            field = value
            assert(this.validateCount())
        }
    var containsOrigin: Boolean = false
        private set
    var nextDirection: Vec2 = Vec2()
        private set


    // Methods
    fun add(vector: Vec2) {
        when (this.count) {
            0 -> a = vector
            1 -> {
                b = a
                a = vector
            }
            2 -> {
                c = b
                b = a
                a = vector
            }
            else -> throw IllegalStateException("Cannot add a vector unless there are 0-2 already present. Current: ${this.count}.")
        }
        this.count += 1
    }

    fun updateState() {
        if (this.count < 2 || this.count > 3) {
            throw IllegalStateException("State updates are only valid in a Simplex with 2-3 points. Current: ${this.count}.")
        }

        when (this.count) {
            2 -> {
                val a = this.a!!
                val b = this.b!!

                val ao = origin - a
                val ab = b - a
                val abNorm = tripleProduct(ab, ao, ab)

                this.nextDirection = abNorm
            }
            3 -> {
                // Triangle Case
                val a = this.a!!
                val b = this.b!!
                val c = this.c!!

                // Edges
                val ao = origin - a
                val ab = b - a
                val ac = c - a

                // Normals
                val abNorm = tripleProduct(ac, ab, ab)
                val acNorm = tripleProduct(ab, ac, ac)

                // Is the origin in R4?
                if (abNorm.dot(ao) > 0) {
                    // Remove point C
                    this.removeC()
                    this.nextDirection = abNorm
                } else {
                    // Is the origin in R3?
                    if (acNorm.dot(ao) > 0) {
                        // Remove Point B
                        this.removeB()
                        this.nextDirection = acNorm
                    } else {
                        // The origin is in the simplex; we are done!
                        this.containsOrigin = true
                        this.nextDirection = Vec2()
                    }
                }
            }
        }
    }


    // Private Methods
    private fun removeB() {
        b = c
        c = null
        this.count -= 1
    }

    private fun removeC() {
        c = null
        this.count -= 1
    }

    private fun validateCount(): Boolean {
        when (this.count) {
            0 -> return a == null && b == null && c == null
            1 -> return a != null && b == null && c == null
            2 -> return a != null && b != null && c == null
            3 -> return a != null && b != null && c != null
            else -> return false // Count is not valid if it isn't 0 - 3!
        }
    }

    companion object {
        // Something is 0 if it is within one-hundredth of a pixel
        val COMPARISON_BOUND = (1f / 32f) / 100f //PhysicsSystem.METERS_PER_PIXEL / 100f

        val origin = Vec2(0f, 0f)

        /**
         * Evaluates (U x V) x W
         */
        private fun tripleProduct(u: Vec2, v: Vec2, w: Vec2): Vec2 = (v * w.dot(u)) - (u * w.dot(v))
    }

}