package com.jupiter.goblin.input

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.jupiter.ganymede.event.Event
import com.jupiter.ganymede.event.EventWrapper
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
object GoblinInput : InputMultiplexer() {
    enum class InputActions {
        LEFT,
        RIGHT,
        UP,
        DOWN,
        JUMP,
        ATTACK,
        DEFEND,
        SPECIAL,
        PAUSE;

        private val firedEvent: Event<InputActions> = Event()
        val fired = EventWrapper(firedEvent)

        fun fire() = this.firedEvent.dispatch(this)
    }

    init {
        this.addProcessor(0, DefaultGoblinInput)
    }
}

object DefaultGoblinInput : InputAdapter() {

    // Properties
    private val keysDown = hashSetOf<Int>()
    private val keysDownThisFrame = hashSetOf<Int>()
    private val keysUpThisFrame = hashSetOf<Int>()

    private val mouseButtonsDown = hashSetOf<Int>()
    private val mouseButtonsDownThisFrame = hashSetOf<Int>()


    // Public Methods
    /**
     * Handles a key being pressed
     * @param keycode The code for the key, as defined in  [com.badlogic.gdx.Input.Keys]
     */
    override fun keyDown(keycode: Int): Boolean {
        this.keysDown.add(keycode)
        this.keysDownThisFrame.add(keycode)
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        this.keysDown.remove(keycode)
        this.keysUpThisFrame.add(keycode)
        return true
    }

    fun processFrame() {
        val config = GoblinMenaceGame.settings.inputMap

        for (code in keysDownThisFrame) {
            config.pressActionMap
                    .filter { pair -> pair.key.code == code }
                    .forEach { pair ->
                        pair.value.forEach { it.fire() }
                    }
        }
        this.keysDownThisFrame.clear()

        for (code in keysDown) {
            config.holdActionMap
                    .filter { pair -> pair.key.code == code }
                    .forEach { pair ->
                        pair.value.forEach { it.fire() }
                    }
        }

        for (code in keysUpThisFrame) {
            config.releaseActionMap
                    .filter { pair -> pair.key.code == code }
                    .forEach { pair ->
                        pair.value.forEach { it.fire() }
                    }
        }
        this.keysUpThisFrame.clear()
    }

}