package com.jupiter.goblin.entity

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Disposable
import com.jupiter.goblin.CameraController
import com.jupiter.goblin.GoblinMenaceGame
import com.jupiter.goblin.io.Logger
import com.jupiter.goblin.util.AccumulatingTimer

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
    val PIXELS_PER_METER: Float = 32.0f
    val METERS_PER_PIXEL: Float = 1.0f / PIXELS_PER_METER

    /**
     * The acceleration due to gravity. Units are m / s^2
     */
    val Gravity = Vector2(0.0f, -15f)

    /**
     * Whether or not the entity world will use the sleep optimization
     */
    val DO_SLEEP: Boolean = true


    // Immutable Properties
    // Lazy is used to avoid this attempting to read settings before they are read in
    private val physicsTimer by lazy { AccumulatingTimer(1.0f / GoblinMenaceGame.settings.physicsRefreshRate, { deltaT -> world.step(deltaT, 6, 2) }) }
    val world = World(Gravity, DO_SLEEP)


    init {
        Box2D.init()
    }


    /**
     * Performs physics system calculations.
     *
     * @param delta The time elapsed, in seconds, since the last update() call
     */
    override fun update(delta: Float) {
        physicsTimer.tick(delta)
        //        world.step(delta, 6, 2)
    }

    inline fun create(init: PhysicsComponentBuilder.() -> Unit): PhysicsComponent {
        val builder = PhysicsComponentBuilder()
        builder.init()
        return builder.toComponent()
    }

    inline fun polygon(init: PolygonPhysicsComponentBuilder.() -> Unit): PhysicsComponent {
        val builder = PolygonPhysicsComponentBuilder()
        builder.init()
        return builder.toComponent()
    }

    inline fun edge(init: EdgePhysicsComponentBuilder.() -> Unit): PhysicsComponent {
        val builder = EdgePhysicsComponentBuilder()
        builder.init()
        return builder.toComponent()
    }

    inline fun chain(init: ChainPhysicsComponentBuilder.() -> Unit): PhysicsComponent {
        val builder = ChainPhysicsComponentBuilder()
        builder.init()
        return builder.toComponent()
    }


    // Disposable Implementation
    override fun dispose() {
        world.dispose()
    }

}

open class PhysicsComponentBuilder {

    var shape: Shape? = null

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
        applyDefaults(bodyDef)
        bodyDef.bodyFunc()
        val bodyActual = PhysicsSystem.world.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        applyDefaults(fixtureDef)
        fixtureDef.fixtureFunc()
        fixtureDef.shape = curShape
        val fixtureActual = bodyActual.createFixture(fixtureDef)

        val component = PhysicsComponent(bodyActual, fixtureActual)

        curShape.dispose()

        return component
    }

    fun applyDefaults(body: BodyDef) {
        body.fixedRotation = DEFAULT_FIXED_ROTATION
    }

    fun applyDefaults(fixture: FixtureDef) {
        fixture.friction = DEFAULT_FRICTION
    }

    companion object {
        val DEFAULT_FIXED_ROTATION: Boolean = true
        val DEFAULT_FRICTION: Float = 1.0f
    }
}

fun PolygonShape.fitToSprite(sprite: Sprite) {
    setAsBox(sprite.width / 2.0f, sprite.height / 2.0f)
}

class PolygonPhysicsComponentBuilder : PhysicsComponentBuilder() {
    inline fun shape(init: PolygonShape.() -> Unit) {
        this.shape = PolygonShape().apply { init() }
    }
}

class EdgePhysicsComponentBuilder : PhysicsComponentBuilder() {
    inline fun shape(init: EdgeShape.() -> Unit) {
        this.shape = EdgeShape().apply { init() }
    }
}

class ChainPhysicsComponentBuilder : PhysicsComponentBuilder() {
    inline fun shape(init: ChainShape.() -> Unit) {
        this.shape = ChainShape().apply { init() }
    }
}

class PhysicsComponent(val body: Body, val fixture: Fixture) : Component {
    fun lockToCenter(): CameraController = object : CameraController {
        override fun setPosition(camera: Camera) {
            camera.position.set(body.position.x, body.position.y, 0.0f)
        }
    }
}

object PhysicsFamilyListener : EntityListener {

    override fun entityRemoved(entity: Entity) {

    }

    override fun entityAdded(entity: Entity) {
        Logger.debug { "Entity ${entity.id} has become part of the physics family!" }
        Mappers.physics[entity].body.userData = entity
    }

}

object PhysicsEngineListener : EntityListener {

    override fun entityRemoved(entity: Entity) {
        if (Families.physics.matches(entity)) {
            Logger.debug { "Entity ${entity.id} has been removed from the engine and has a Physics component." }
            PhysicsSystem.world.destroyBody(Mappers.physics[entity].body)
        }
    }

    override fun entityAdded(entity: Entity) {

    }

}