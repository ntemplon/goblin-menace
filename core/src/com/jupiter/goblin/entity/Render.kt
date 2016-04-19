package com.jupiter.goblin.entity

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Sprite
import com.jupiter.goblin.GoblinMenaceGame
import com.jupiter.goblin.entity.physics.PhysicsSystem

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
object PhysicsBindingSystem : IteratingSystem(Families.physicsBound, GoblinMenaceGame.PhysicsBindingSystemPriority) {

    /**
     * @param entity The entity that will be processed by this method call
     * @param deltaTime The amount of time elapsed, in seconds, since the last round of processEntity() calls
     */
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val physics = Mappers.physics[entity]
        val render = Mappers.render[entity]
        val binding = Mappers.physicsBinding[entity]

        render.sprite.x = physics.shape.position.x + binding.xOff - render.sprite.width / 2.0f
        render.sprite.y = physics.shape.position.y + binding.yOff - render.sprite.height / 2.0f
    }

}


/**
 * @property sprite The sprite that will be rendered for the entity containing this component
 */
class RenderComponent @JvmOverloads constructor(val sprite: Sprite, val scaling: Float = 1.0f) : Component {

    init {
        sprite.setSize(sprite.width * PhysicsSystem.METERS_PER_PIXEL * scaling, sprite.height * PhysicsSystem.METERS_PER_PIXEL * scaling)
        sprite.setOriginCenter()
    }

}


/**
 * @property xOff The X offset of the sprite from the physics component, in meters
 * @property yOff The Y offset of the sprite from the physics component, in meters
 */
class PhysicsBindingComponent @JvmOverloads constructor(val xOff: Float = 0f, val yOff: Float = 0f) : Component {}