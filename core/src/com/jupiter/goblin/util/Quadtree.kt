package com.jupiter.goblin.util

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
 * @property bounds the bounds of the area covered by this [QuadTree]
 * @property maxObjects the maximum number of objects per leaf
 * @property maxLevels the maximum number of levels of sub-trees beneath this one
 */
class QuadTree<T : RectangleBounded>(val bounds: RectangleBounded, val maxObjects: Int = 10, val maxLevels: Int = 5) : RectangleBounded by bounds {

    private val _items: MutableSet<T> = mutableSetOf()

    private var topLeft: QuadTree<T>? = null
    private var topRight: QuadTree<T>? = null
    private var bottomRight: QuadTree<T>? = null
    private var bottomLeft: QuadTree<T>? = null
    private var leaves: List<QuadTree<T>>? = null


    val items: Set<T> = _items


    // Private Methods
    private fun createChildren() {
        val childWidth = this.bounds.halfWidth / 2.0f
        val childHeight = this.bounds.halfHeight / 2.0f
        val childLeft = this.bounds.position.x - childWidth
        val childRight = this.bounds.position.x + childWidth
        val childTop = this.bounds.position.y + childHeight
        val childBottom = this.bounds.position.y - childHeight

        val topLeft = QuadTree<T>(SimpleRectangleBounds(
                Vec2(childLeft, childTop),
                childWidth,
                childHeight
        ))
        this.topLeft = topLeft

        val topRight = QuadTree<T>(SimpleRectangleBounds(
                Vec2(childRight, childTop),
                childWidth,
                childHeight
        ))
        this.topRight = topRight

        val bottomRight = QuadTree<T>(SimpleRectangleBounds(
                Vec2(childRight, childBottom),
                childWidth,
                childHeight
        ))
        this.bottomRight = bottomRight

        val bottomLeft = QuadTree<T>(SimpleRectangleBounds(
                Vec2(childLeft, childBottom),
                childWidth,
                childHeight
        ))
        this.bottomLeft = bottomLeft

        this.leaves = listOf(topLeft, topRight, bottomRight, bottomLeft)
    }

}

interface RectangleBounded {
    val position: Vec2
    val halfWidth: Float
    val halfHeight: Float
}

class SimpleRectangleBounds(override val position: Vec2, override val halfWidth: Float, override val halfHeight: Float) : RectangleBounded {}