package com.jupiter.goblin.entity.physics

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.math.Vector2
import com.jupiter.goblin.GoblinMenaceGame
import com.jupiter.goblin.entity.Families
import com.jupiter.goblin.entity.Mappers
import com.jupiter.goblin.util.AccumulatingTimer
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
object PhysicsSystem : EntitySystem(GoblinMenaceGame.PhysicsSystemPriority) {

    // Constants
    /**
     * The number of pixels in a meter (big number)
     */
    val PIXELS_PER_METER: Float = 32.0f
    /**
     * The number of meters in a pixel (small number)
     */
    val METERS_PER_PIXEL: Float = 1.0f / PIXELS_PER_METER

    /**
     * The acceleration due to gravity. Units are m / s^2
     */
    val Gravity = Vector2(0.0f, -15f)


    // Properties
    private val timer by lazy { AccumulatingTimer(1f / GoblinMenaceGame.settings.physicsRefreshRate, { delta -> this.advanceState(delta) }) }
    private val physicsEntities by lazy { GoblinMenaceGame.entityEngine.getEntitiesFor(Families.physics) }


    // Public Methods
    /**
     * Updates the physics systems
     * @param deltaTime the amount of time elapsed, in seconds, since the last update
     */
    override fun update(deltaTime: Float) {
        this.timer.tick(deltaTime)
    }

    /**
     * Re-reads the physics refresh rate from the GoblinMenaceGame settings
     */
    fun refreshUpdateInterval() {
        timer.interval = 1f / GoblinMenaceGame.settings.physicsRefreshRate
    }


    // Private Methods
    private fun advanceState(delta: Float) {
        for (entity in physicsEntities) {
            val item = Mappers.physics[entity].item
            when (item) {
                is PhysicsItem.KinematicItem -> {
                    val cur = item.position
                    item.position = Vec2(cur.x + item.velocity.x * delta, cur.y + item.velocity.y * delta)
                }
                is PhysicsItem.DynamicItem -> {

                }
            }
        }
    }

    private fun getPoly(obj: PolygonMapObject): Shape.Polygon {
        val source = obj.polygon
        return Shape.Polygon(
                Vec2(source.x * METERS_PER_PIXEL, source.y * METERS_PER_PIXEL),
                (0..(source.vertices.size - 2) step 2).map {
                    Vec2(source.vertices[it] * METERS_PER_PIXEL, source.vertices[it + 1] * METERS_PER_PIXEL)
                }
        )
    }


    // Utility Extensions
    fun PolygonMapObject.asStatic(): PhysicsComponent {
        val polygon = this.polygon
        val vertices = polygon.vertices
        return PhysicsComponent(PhysicsItem.StaticItem(Platform(vertices.withIndex().map {
            val index = it.index
            if (index % 2 == 0) {
                Vec2((vertices[index] + polygon.x) / PhysicsSystem.PIXELS_PER_METER, (polygon.y + vertices[index + 1]) / PhysicsSystem.PIXELS_PER_METER)
            } else {
                null
            }
        }.filterNotNull())))
    }

}