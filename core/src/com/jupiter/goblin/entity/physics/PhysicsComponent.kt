package com.jupiter.goblin.entity.physics

import com.badlogic.ashley.core.Component
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
class PhysicsComponent(val item: PhysicsItem) : Component {

}

sealed class PhysicsItem(val shape: PhysicsRenderer.PhysicsRenderable) {

    /**
     * Creates a new [PhysicsComponent] containing this [PhysicsItem]
     */
    fun component(): PhysicsComponent = PhysicsComponent(this)


    /**
     * A class for static terrain.  These cannot move, but are not restricted to being rectangular.
     */
    class StaticItem(val platform: Platform) : PhysicsItem(platform) {

    }

    /**
     * A class for moving platforms, etc.  These can move, but do not collide with anything.
     */
    class KinematicItem(bounds: Rectangle) : PhysicsItem(bounds) {
        val bounds: Rectangle = bounds
        var position: Vec2 = bounds.center
        var velocity: Vec2 = Vec2(0f, 0f)
    }

    /**
     * A class for players, enemies, etc.  These move and collide with Kinematic and Static items, but do not collide
     * with each other
     */
    class DynamicItem(bounds: Rectangle) : PhysicsItem(bounds) {
        val bounds: Rectangle = bounds
        var position: Vec2 = this.bounds.center
        var velocity: Vec2 = Vec2(0f, 0f)
    }

}