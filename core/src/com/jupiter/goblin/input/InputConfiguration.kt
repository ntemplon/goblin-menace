package com.jupiter.goblin.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.jupiter.ganymede.event.Event
import com.jupiter.ganymede.event.EventWrapper
import com.jupiter.goblin.io.Logger

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
class InputConfiguration {
    public val inputMap: Map<GoblinInput.InputActions, List<UserAction>>
        get() = _inputMap

    private var _inputMap: MutableMap<GoblinInput.InputActions, List<UserAction>> = linkedMapOf(
            GoblinInput.InputActions.LEFT to listOf(
                    Input.Keys.A.toKeyAction(UserAction.ActionTiming.POLL),
                    Input.Keys.LEFT.toKeyAction(UserAction.ActionTiming.POLL)
            ),
            GoblinInput.InputActions.RIGHT to listOf(
                    Input.Keys.D.toKeyAction(UserAction.ActionTiming.POLL),
                    Input.Keys.RIGHT.toKeyAction(UserAction.ActionTiming.POLL)
            ),
            GoblinInput.InputActions.UP to listOf(
                    Input.Keys.W.toKeyAction(UserAction.ActionTiming.POLL),
                    Input.Keys.UP.toKeyAction(UserAction.ActionTiming.POLL)
            ),
            GoblinInput.InputActions.DOWN to listOf(
                    Input.Keys.S.toKeyAction(UserAction.ActionTiming.POLL),
                    Input.Keys.DOWN.toKeyAction(UserAction.ActionTiming.POLL)
            ),
            GoblinInput.InputActions.ATTACK to listOf(
                    Input.Keys.J.toKeyAction(UserAction.ActionTiming.PRESS),
                    Input.Buttons.LEFT.toMouseAction(UserAction.ActionTiming.PRESS)
            ),
            GoblinInput.InputActions.DEFEND to listOf(
                    Input.Keys.K.toKeyAction(UserAction.ActionTiming.PRESS)
            ),
            GoblinInput.InputActions.SPECIAL to listOf(
                    Input.Keys.L.toKeyAction(UserAction.ActionTiming.PRESS)
            ),
            GoblinInput.InputActions.PAUSE to listOf(
                    Input.Keys.ESCAPE.toKeyAction(UserAction.ActionTiming.PRESS)
            )
    )
        get() = field
        set(value) {
            field = value
            this.refreshInputMaps()
        }

    init {
        this.refreshInputMaps()
    }

    private fun refreshInputMaps() {

    }
}

sealed class UserAction(code: Int, timing: ActionTiming) : Json.Serializable {

    enum class ActionTiming {
        PRESS,
        POLL,
        RELEASE
    }

    // Properties
    var code: Int = code
        private set

    var timing: ActionTiming = timing
        private set


    // Methods
    override fun write(json: Json) {
        json.writeValue(TYPE_KEY, this.javaClass.simpleName)
        json.writeValue(CODE_KEY, code)
        json.writeValue(TIMING_KEY, timing.toString())
    }

    override fun read(json: Json, jsonData: JsonValue) {
        if (jsonData.has(CODE_KEY)) {
            try {
                this.code = jsonData.getInt(CODE_KEY)
            } catch (ex: Exception) {
                Logger.warn { "Error parsing value \"${jsonData.get(CODE_KEY).toString()}\" for key \"$CODE_KEY.\"" }
            }
        }

        if (jsonData.has(TIMING_KEY)) {
            try {
                this.timing = ActionTiming.valueOf(jsonData.getString(TIMING_KEY))
            } catch (ex: Exception) {
                Logger.warn { "Error parsing value \"${jsonData.get(TIMING_KEY).toString()}\" for key \"$TIMING_KEY.\"" }
            }
        }
    }

    companion object {
        private val TYPE_KEY = "type"
        private val CODE_KEY = "code"
        private val TIMING_KEY = "timing"
    }

    class KeyAction(code: Int = Input.Keys.ENTER, timing: ActionTiming = ActionTiming.PRESS) : UserAction(code, timing) {

    }

    class MouseAction(button: Int = Input.Buttons.LEFT, timing: ActionTiming = ActionTiming.PRESS) : UserAction(button, timing) {
        val button: Int
            get() = super.code
    }
}

fun Int.toKeyAction(timing: UserAction.ActionTiming): UserAction = UserAction.KeyAction(this, timing)
fun Int.toMouseAction(timing: UserAction.ActionTiming): UserAction = UserAction.MouseAction(this, timing)