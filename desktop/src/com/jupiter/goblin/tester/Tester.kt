package com.jupiter.goblin.tester

import com.badlogic.gdx.math.Vector2
import com.jupiter.goblin.entity.physics.Shape
import com.jupiter.goblin.util.Vec2
import com.jupiter.goblin.util.revolve
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
fun main(args: Array<String>) {

    println("Starting!")

    val vec1 = Vector2(2f.sqrt(), 2f.sqrt())
    val vec2 = vec1.revolve(Math.PI.toFloat() / 2f)

    val shape1 = Shape.Polygon(
            position = Vec2(0f, 0f),
            verts = listOf(
                    Vec2(0f, 0f),
                    Vec2(1f, 0f),
                    Vec2(1f, 1f),
                    Vec2(0f, 1f)
            )
    )

    val shape2 = Shape.Polygon(
            position = Vec2(1.0f, 0.8f),
            verts = listOf(
                    Vec2(0f, 0f),
                    Vec2(1f, 0f),
                    Vec2(1f, 1f)
            )
    )

    val shape3 = shape1 - shape2
    println(shape3)
    println(shape3.contains(Vec2(0f, 0f)))

}