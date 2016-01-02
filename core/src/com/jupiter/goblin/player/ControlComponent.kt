package com.jupiter.goblin.player

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.jupiter.goblin.entity.Families
import com.jupiter.goblin.entity.Mappers
import com.jupiter.goblin.util.ControlledAction

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
class ControlComponent : Component {

    public var walkLeft: ControlledAction = NoOp
    public var walkRight: ControlledAction = NoOp
    public var walkUp: ControlledAction = NoOp
    public var walkDown: ControlledAction = NoOp
    public var jump: ControlledAction = NoOp

    public var attack: ControlledAction = NoOp
    public var defend: ControlledAction = NoOp
    public var special: ControlledAction = NoOp

    companion object {
        public val NoOp: ControlledAction = ControlledAction(1.0f, {})

        public val DEFAULT_JUMP_SPEED = 10f
        public val DEFAULT_JUMP_DELAY = 0.1f

        fun default(entity: Entity): ControlComponent {
            val cntrl = ControlComponent()

            cntrl.jump = ControlledAction(DEFAULT_JUMP_DELAY, {
                if (Families.Physics.matches(entity)) {
                    val body = Mappers.Physics[entity].body
                    body.applyLinearImpulse(0.0f, DEFAULT_JUMP_SPEED * body.mass, body.position.x, body.position.y, true)
                }
            })

            return cntrl
        }
    }

}