package com.jupiter.goblin.entity.physics

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import com.jupiter.goblin.GoblinMenaceGame
import com.jupiter.goblin.entity.Families
import com.jupiter.goblin.entity.Mappers

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
object PhysicsRenderer {

    private val physEnts = GoblinMenaceGame.entityEngine.getEntitiesFor(Families.physics)
    private val render = ShapeRenderer().apply { setAutoShapeType(true) }


    fun render(projectionMatrix: Matrix4) {
        render.projectionMatrix = projectionMatrix
        render.begin()

        // Render each physics shape
        for (ent in physEnts) {
            render.polygon(Mappers.physics[ent].item.shape.physicsVertices)
        }

        render.end()
    }


    interface PhysicsRenderable {
        val physicsVertices: FloatArray
    }

}