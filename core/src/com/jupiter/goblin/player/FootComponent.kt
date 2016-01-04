package com.jupiter.goblin.player

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.joints.WeldJoint
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef
import com.jupiter.goblin.GoblinMenaceGame
import com.jupiter.goblin.entity.Families
import com.jupiter.goblin.entity.FrameFunctionComponent
import com.jupiter.goblin.entity.Mappers
import com.jupiter.goblin.entity.PhysicsSystem

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
class FootComponent(val owner: Entity) : Component {

    // Initialization
    init {
        if (!Families.physics.matches(owner)) {
            throw IllegalArgumentException("Owner must be a physics-enabled entity. Be sure to add the physics component before the foot component.")
        }
    }


    // Properties
    private val currentContacts = arrayListOf<Body>()

    private val parent = Mappers.physics[owner].fixture
    private val parentPolygon = parent.shape as? PolygonShape

    val parentWidth: Float
    val parentHeight: Float

    val fixture: Fixture

    init {
        val vertex = Vector2()
        parentPolygon?.getVertex(0, vertex)

        parentWidth = vertex.x
        parentHeight = vertex.y

        val shape = PolygonShape().apply {
            setAsBox(parentWidth, FOOT_HALF_HEIGHT, Vector2(0f, parentHeight), 0f)
        }

        val fixtureDef: FixtureDef = FixtureDef().apply {
            this.shape = shape
            isSensor = true
        }

        fixture = parent.body.createFixture(fixtureDef)
        fixture.userData = this

        shape.dispose()
    }

    val standingOnGround: Boolean
        get() = this.currentContacts.any { it.type != BodyDef.BodyType.DynamicBody }


    companion object {
        private val FOOT_HALF_HEIGHT = 0.05f // In meters
        private val contactListener = object : ContactListener {

            override fun endContact(contact: Contact) {
                val userDataA = contact.fixtureA.userData as? FootComponent
                val userDataB = contact.fixtureB.userData as? FootComponent

                if (userDataA != null && (contact.fixtureA === userDataA.fixture)) {
                    // In this case, fixtureB is colliding with userDataA
                    userDataA.currentContacts.remove(contact.fixtureB.body)
                } else if (userDataB != null && (contact.fixtureB === userDataB.fixture)) {
                    // fixtureA is the "ground"
                    userDataB.currentContacts.remove(contact.fixtureA.body)
                }
            }

            override fun beginContact(contact: Contact) {
                val userDataA = contact.fixtureA.userData as? FootComponent
                val userDataB = contact.fixtureB.userData as? FootComponent

                if (userDataA != null && (contact.fixtureA === userDataA.fixture)) {
                    // In this case, fixtureB is colliding with userDataA
                    userDataA.currentContacts.add(contact.fixtureB.body)
                } else if (userDataB != null && (contact.fixtureB === userDataB.fixture)) {
                    // fixtureA is the "ground"
                    userDataB.currentContacts.add(contact.fixtureA.body)
                }
            }

            override fun preSolve(contact: Contact, oldManifold: Manifold) {

            }

            override fun postSolve(contact: Contact, impulse: ContactImpulse) {

            }
        }

        init {
            PhysicsSystem.world.setContactListener(contactListener)
        }
    }
}