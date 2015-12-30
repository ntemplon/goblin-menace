package com.jupiter.goblin.entity

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Disposable
import com.jupiter.goblin.CameraController
import com.jupiter.goblin.GoblinMenaceGame

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

/**
 * The physics system for the game.
 *
 * All units are meters, kilograms, and seconds
 */
object PhysicsSystem : EntitySystem(GoblinMenaceGame.PhysicsSystemPriority), Disposable {

    // Constants
    /**
     * Units are hz
     */
    val PhysicsRefreshRate: Float = 20.0f

    val PixelsPerMeter: Float = 32.0f
    val MetersPerPixel: Float = 1.0f / PixelsPerMeter

    /**
     * Units are m / s^2
     */
    val Gravity = Vector2(0.0f, -9.8f)

    /**
     * Whether or not the entity world will use the sleep optimization
     */
    val DoSleep: Boolean = true


    // Immutable Properties
    val world = World(Gravity, DoSleep)


    init {
        Box2D.init()
    }


    /**
     * Performs physics system calculations.
     *
     * @param delta The time elapsed, in seconds, since the last update() call
     */
    override fun update(delta: Float) {
        world.step(delta, 6, 2)
    }

    fun create(init: PhysicsComponentBuilder.() -> Unit): PhysicsComponent {
        val builder = PhysicsComponentBuilder()
        builder.init()
        return builder.toComponent()
    }

    fun polygon(init: PolygonPhysicsComponentBuilder.() -> Unit): PhysicsComponent {
        val builder = PolygonPhysicsComponentBuilder()
        builder.init()
        return builder.toComponent()
    }

    fun edge(init: EdgePhysicsComponentBuilder.() -> Unit): PhysicsComponent {
        val builder = EdgePhysicsComponentBuilder()
        builder.init()
        return builder.toComponent()
    }


    // Disposable Implementation
    override fun dispose() {
        world.dispose()
    }

}

open class PhysicsComponentBuilder {

    public var shape: Shape? = null

    protected var bodyFunc: BodyDef.() -> Unit = {}
    protected var fixtureFunc: FixtureDef.() -> Unit = {}


    fun body(init: BodyDef.() -> Unit) {
        bodyFunc = init
    }

    fun fixture(init: FixtureDef.() -> Unit) {
        fixtureFunc = init
    }


    fun toComponent(): PhysicsComponent {
        val curShape = this.shape ?: throw IllegalStateException("\"Shape\" variable has not been set!")

        val bodyDef = BodyDef()
        bodyDef.fixedRotation = true
        bodyDef.bodyFunc()
        val bodyActual = PhysicsSystem.world.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        fixtureDef.fixtureFunc()
        fixtureDef.shape = curShape
        val fixtureActual = bodyActual.createFixture(fixtureDef)

        val component = PhysicsComponent(bodyActual, fixtureActual)

        curShape.dispose()

        return component
    }
}

fun PolygonShape.fitToSprite(sprite: Sprite) {
    setAsBox(sprite.width / 2.0f, sprite.height / 2.0f)
}

class PolygonPhysicsComponentBuilder : PhysicsComponentBuilder() {
    fun shape(init: PolygonShape.() -> Unit) {
        this.shape = PolygonShape().apply { init() }
    }
}

class EdgePhysicsComponentBuilder : PhysicsComponentBuilder() {
    fun shape(init: EdgeShape.() -> Unit) {
        this.shape = EdgeShape().apply { init() }
    }
}

class PhysicsComponent(val body: Body, val fixture: Fixture) : Component {
    fun lockToCenter(): CameraController = object: CameraController {
        override fun setPosition(camera: Camera) {
            camera.position.set(body.position.x, body.position.y, 0.0f)
        }
    }
}