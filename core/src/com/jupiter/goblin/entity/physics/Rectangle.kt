package com.jupiter.goblin.entity.physics

import com.jupiter.goblin.util.Vec2

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
class Rectangle(center: Vec2, val halfWidth: Float, val halfHeight: Float) {

    // Properties
    var center: Vec2 = center
        get() = field
        set(value) {
            field = value
            this.left = this.center.x - this.halfWidth
            this.right = this.center.x + this.halfWidth
            this.top = this.center.y + this.halfHeight
            this.bottom = this.center.y - this.halfHeight
        }

    var left: Float = this.center.x - this.halfWidth
        get
        private set

    var right: Float = this.center.x + this.halfWidth
        get
        private set

    var top: Float = this.center.y + this.halfHeight
        get
        private set

    var bottom: Float = this.center.y - this.halfHeight
        get
        private set

    val vertices: FloatArray
        get() = floatArrayOf(this.right, this.top, this.left, this.top, this.left, this.bottom, this.right, this.bottom)


    // Public Methods
    fun intersects(other: Rectangle): Boolean {
        return this.left < other.right &&
                this.right > other.left &&
                this.top > other.bottom &&
                this.bottom < other.top
    }

    override fun toString(): String {
        return "[Bounding Box: Position ${this.center.toString()}, Half Width ${this.halfWidth}, Half Height ${this.halfHeight}]"
    }

    override fun equals(other: Any?): Boolean {
        if (other is Rectangle) {
            return other.center == this.center &&
                    other.halfWidth == this.halfWidth &&
                    other.halfHeight == this.halfHeight
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        var hash: Int = this.center.x.hashCode()
        hash = hash * 31 + this.center.y.hashCode()
        hash = hash * 31 + this.halfWidth.hashCode()
        hash = hash * 31 + this.halfHeight.hashCode()
        return hash
    }

}