package com.jupiter.goblin.player

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.jupiter.goblin.entity.Families
import com.jupiter.goblin.input.GoblinInput
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

    // Properties
    public var walkLeft: ControlledAction = NoOp
    public var walkRight: ControlledAction = NoOp
    public var walkUp: ControlledAction = NoOp
    public var walkDown: ControlledAction = NoOp
    public var jump: ControlledAction = NoOp

    public var attack: ControlledAction = NoOp
    public var defend: ControlledAction = NoOp
    public var special: ControlledAction = NoOp

    private val walkLeftFunc = { walkLeft.request() }
    private val walkRightFunc = { walkRight.request() }
    private val walkUpFunc = { walkUp.request() }
    private val walkDownFunc = { walkDown.request() }
    private val jumpFunc = { jump.request() }

    private val attackFunc = { attack.request() }
    private val defendFunc = { defend.request() }
    private val specialFunc = { special.request() }


    // Public Methods
    fun bind() {
        GoblinInput.InputActions.LEFT.fired.addListener { walkLeftFunc.invoke() }
        GoblinInput.InputActions.RIGHT.fired.addListener { walkRightFunc.invoke() }
        GoblinInput.InputActions.UP.fired.addListener { walkUpFunc.invoke() }
        GoblinInput.InputActions.DOWN.fired.addListener { walkDownFunc.invoke() }
        GoblinInput.InputActions.JUMP.fired.addListener { jumpFunc.invoke() }

        GoblinInput.InputActions.ATTACK.fired.addListener { attackFunc.invoke() }
        GoblinInput.InputActions.DEFEND.fired.addListener { defendFunc.invoke() }
        GoblinInput.InputActions.SPECIAL.fired.addListener { specialFunc.invoke() }
    }

    fun unbind() {
        GoblinInput.InputActions.LEFT.fired.removeListener { walkLeftFunc.invoke() }
        GoblinInput.InputActions.RIGHT.fired.removeListener { walkRightFunc.invoke() }
        GoblinInput.InputActions.UP.fired.removeListener { walkUpFunc.invoke() }
        GoblinInput.InputActions.DOWN.fired.removeListener { walkDownFunc.invoke() }
        GoblinInput.InputActions.JUMP.fired.removeListener { jumpFunc.invoke() }

        GoblinInput.InputActions.ATTACK.fired.removeListener { attackFunc.invoke() }
        GoblinInput.InputActions.DEFEND.fired.removeListener { defendFunc.invoke() }
        GoblinInput.InputActions.SPECIAL.fired.removeListener { specialFunc.invoke() }
    }


    companion object {
        public val NoOp: ControlledAction = ControlledAction(1.0f, {})

        public val DEFAULT_JUMP_SPEED = 8f
        public val DEFAULT_JUMP_DELAY = 0.1f

        public val DEFAULT_WALK_DELAY = 0f
        public val DEFAULT_WALK_TICK_ACCEL_FRAC = 0.25f
        public val DEFAULT_WALK_SPEED = 5f

        fun default(entity: Entity): ControlComponent {
            val cntrl = ControlComponent()

            cntrl.jump = ControlledAction(DEFAULT_JUMP_DELAY, {
                if (Families.physics.matches(entity)) {
                    // Jump here
                }
            }, setOf(
                    {
                        true // Return true if standing on the ground
                    }
            ))

            cntrl.walkRight = ControlledAction(DEFAULT_WALK_DELAY, {
                if (Families.physics.matches(entity)) {
                    // Walk RIGHT here
                }
            })

            cntrl.walkLeft = ControlledAction(DEFAULT_WALK_DELAY, {
                if (Families.physics.matches(entity)) {
                    // Walk LEFT here
                }
            })

            return cntrl
        }
    }
}