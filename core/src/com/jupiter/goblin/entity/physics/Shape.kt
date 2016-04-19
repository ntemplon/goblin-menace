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

    abstract val maxRadius: Float

    /**
     * The position of the polygon in world coordinates
     */
    var position: Vec2 = position
        get() = field
        set(value) {
            field = value
            this.onPositionChanged()
        }


    fun circleIntersects(other: Polygon): Boolean {
        val combinedDistance = this.maxRadius + other.maxRadius
        return this.position.distance2(other.position) <= (combinedDistance * combinedDistance)
    }


    protected open fun onPositionChanged() {

    }


    /**
     * A shape representing a polygon
     * @param position the position of the polygon
     * @param verts the vertices of the polygon, in body coordinates. It is assumed that they form a convex
     * polygon, and that they are in counter-clockwise order (the right side of each edge is the exterior).
     */
    class Polygon(position: Vec2, verts: List<Vec2>) : Shape(position) {

        /**
         * The vertices of the polygon, in counter-clockwise order, in local coordinates (with respect to position)
         */
        val vertices: List<Vec2>

        /**
         * The vertices of the polygon, in counter-clockwise order, in local coordinates (with respect to position)
         * In a float-array form for traditional physics calculations
         */
        val floatVertices: FloatArray

        /**
         * The edges of the polygon, in counter-clockwise order, in local coordinates (with respect to position)
         */
        val edges: List<Edge>

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

            this.floatVertices = this.vertices
                    .flatMap { listOf(it.x, it.y) }
                    .toFloatArray()

            this.edges = this.vertices.withIndex().map {
                if (it.index < this.vertices.size - 1) {
                    Edge(it.value, this.vertices[it.index + 1])
                } else {
                    Edge(it.value, this.vertices[0])
                }
            }
        }

        /**
         * The vertices of the polygon, in counter-clockwise order, in world coordinates
         */
        var worldVertices: List<Vec2> = listOf()
            private set

        /**
         * The vertices of the polygon, in counter-clockwise order, in world coordinates
         * In a float-array form for traditional physics calculations
         */
        var worldFloatVertices: FloatArray = listOf<Float>().toFloatArray()
            private set

        init {
            if (verts.size < 3) {
                throw IllegalArgumentException("The number of vertices must be at least 3!")
            }

            this.computeWorldVertices()
        }

        /**
         * The maximum radial distance of any point from the position
         */
        override val maxRadius = (vertices.map { it.distance2(position) }.max() ?: 0f).sqrt()

        override fun onPositionChanged() {
            this.computeWorldVertices()
        }

        /**
         * Note: if all you need is a boolean yes/no collision, this is 3x faster than the other method!
         */
        fun isCollidingWith(other: Polygon): Boolean {
            return polygonCollision(other)
        }

        fun support(direction: Vec2): Vec2 {
            return this.vertices[this.worldSupportWithIndex(direction).index]
        }

        fun worldSupportWithIndex(direction: Vec2): IndexedValue<Vec2> {
            // Assures us that the !! operator below will never fail
            assert(this.worldVertices.size > 0)

            return this.worldVertices.withIndex().maxBy { it.value.dot(direction) }!!
        }

        fun worldSupport(direction: Vec2): Vec2 {
            // Assures us that the !! operator below will never fail
            assert(this.worldVertices.size > 0)

            return this.worldVertices.maxBy { it.dot(direction) }!!
        }

        fun worldSupport(direction: Vec2, farthest: Boolean): Vec2 {
            // Avoids some errors
            assert(this.worldVertices.size > 0)

            val vertices = arrayListOf<Vec2>()
            var lastDot = 0.0f

            for (vertex in worldVertices) {
                val dot = vertex.dot(direction)
                if (vertices.size == 0) {
                    vertices.add(vertex)
                    lastDot = dot
                } else if (Math.abs(dot - lastDot) < 10e-4f) {
                    vertices.add(vertex)
                } else if (dot > lastDot) {
                    vertices.clear()
                    vertices.add(vertex)
                    lastDot = dot
                }
            }

            if (vertices.size == 1) {
                return vertices[0]
            }

            if (farthest) {
                return vertices.maxBy { it.norm2 }!!
            } else {
                return vertices.minBy { it.norm2 }!!
            }
        }

        fun toWorldCoordinates(bodyCoords: Vec2): Vec2 = bodyCoords + this.position

        /**
         * Tests if the Polygon contains the provided point, using the "same side" test
         * @param point the point to test, in world coordinates
         * @return true if the Polygon contains the point, false if it does not. A point on the boundary gives indeterminate results
         */
        fun contains(point: Vec2): Boolean {
            var posCount = 0
            var negCount = 0

            for (edge in this.edges) {
                val edgeVec = edge.end - edge.start
                val pointVec = this.toWorldCoordinates(edge.start) - point

                val cross = edgeVec.cross(pointVec)
                if (cross < 0) {
                    negCount++
                } else if (cross > 0) {
                    posCount++
                }

                if (posCount > 0 && negCount > 0) {
                    return false
                }
            }

            return true
        }


        /**
         * Calculates the Minkowski sum of two Polygons
         * @param other the Polygon to add to this one
         * @return a new Polygon object containing the Minkowski sum of the two Polygons
         */
        operator fun plus(other: Polygon): Polygon {
            // A and B are terminology from the reference
            val a = this
            val b = other

            class OrderedEdge(val start: Vec2, val end: Vec2) {

                override fun toString(): String {
                    return "[$start to $end]"
                }

            }


            val aSides = a.edges.map { edge ->
                val supp = b.worldSupport(edge.normal, true)
                OrderedEdge(
                        a.toWorldCoordinates(edge.start) + supp,
                        a.toWorldCoordinates(edge.end) + supp
                )
            }

            val bSides = b.edges.map { edge ->
                val supp = a.worldSupport(edge.normal, true)
                OrderedEdge(
                        b.toWorldCoordinates(edge.start) + supp,
                        b.toWorldCoordinates(edge.end) + supp
                )
            }

            val allSides = aSides.union(bSides)
            val startSide = aSides[0]
            var lastSide = startSide
            val sides = arrayListOf(startSide)
            var finished = false
            while (!finished) {
                val nextSide = allSides.minBy { (it.start - lastSide.end).norm2 }!!
                if (nextSide === startSide) {
                    finished = true
                } else {
                    lastSide = nextSide
                    sides.add(lastSide)
                }
            }

            val vertices = sides.map { it.start }

            // Put the position in the center(ish)
            var xMin = vertices[0].x
            var yMin = vertices[0].y
            var xMax = vertices[0].x
            var yMax = vertices[0].y

            for (i in 1..(vertices.size - 1)) {
                val vert = vertices[i]
                if (vert.x < xMin) {
                    xMin = vert.x
                }
                if (vert.y < yMin) {
                    yMin = vert.y
                }
                if (vert.x > xMax) {
                    xMax = vert.x
                }
                if (vert.y > yMax) {
                    yMax = vert.y
                }
            }

            val position = Vec2((xMin + xMax) / 2f, (yMin + yMax) / 2f)
            //        val position = Vec2(0f, 0f)

            return Polygon(position, vertices.map { it - position })
        }


        /**
         * Calculates the Minkowski difference of two Polygons
         * @param other the Polygon to subtract from this one
         * @return a new Polygon object containing the Minkowski difference of the two Polygons
         */
        operator fun minus(other: Polygon): Polygon {
            return this + (other.opposite())
        }


        /**
         * Multiplies all of the vertices by -1
         * @return a new Polygon object containing the opposite of this one
         */
        fun opposite(): Polygon {
            val position = this.position * -1f
            val vertices = this.worldVertices.map {
                (it * -1f) - position
            }

            return Polygon(position, vertices)
        }

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
            this.worldFloatVertices = this.worldVertices.flatMap { listOf(it.x, it.y) }.toFloatArray()
        }


        // Example: "Position: (1.0, 2.0), Vertices: [(0.0, 0.0), (1.0, 0.0), (1.0, 0.0)]"
        override fun toString(): String {
            return "Position: " +
                    this.position.toString() +
                    ", Vertices: [" +
                    this.vertices
                            .map { it.toString() }
                            .joinToString(separator = ", ") +
                    "]"
        }


        companion object {
            // 90 degrees clockwise
            private val EDGE_TO_NORMAL_ANGLE: Float = (Math.PI * -0.5).toFloat()
        }


        class Edge(val start: Vec2, val end: Vec2) {

            val vector = end - start
            val normal = vector.rotate(EDGE_TO_NORMAL_ANGLE).direction

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