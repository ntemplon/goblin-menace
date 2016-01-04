package com.jupiter.goblin.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
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
class InputConfiguration : Json.Serializable {
    private var inputMap: MutableMap<GoblinInput.InputActions, List<UserAction>> = linkedMapOf(
            GoblinInput.InputActions.LEFT to listOf(
                    Input.Keys.A.toKeyAction(UserAction.ActionTiming.HOLD),
                    Input.Keys.LEFT.toKeyAction(UserAction.ActionTiming.HOLD)
            ),
            GoblinInput.InputActions.RIGHT to listOf(
                    Input.Keys.D.toKeyAction(UserAction.ActionTiming.HOLD),
                    Input.Keys.RIGHT.toKeyAction(UserAction.ActionTiming.HOLD)
            ),
            GoblinInput.InputActions.UP to listOf(
                    Input.Keys.W.toKeyAction(UserAction.ActionTiming.HOLD),
                    Input.Keys.UP.toKeyAction(UserAction.ActionTiming.HOLD)
            ),
            GoblinInput.InputActions.DOWN to listOf(
                    Input.Keys.S.toKeyAction(UserAction.ActionTiming.HOLD),
                    Input.Keys.DOWN.toKeyAction(UserAction.ActionTiming.HOLD)
            ),
            GoblinInput.InputActions.JUMP to listOf(
                    Input.Keys.SPACE.toKeyAction(UserAction.ActionTiming.PRESS)
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
            this.refreshActionMap()
        }

    private var actionMap: Map<UserAction, List<GoblinInput.InputActions>> = mapOf()
    public var pressActionMap: Map<UserAction, List<GoblinInput.InputActions>> = mapOf()
        private set
    public var holdActionMap: Map<UserAction, List<GoblinInput.InputActions>> = mapOf()
        private set
    public var releaseActionMap: Map<UserAction, List<GoblinInput.InputActions>> = mapOf()
        private set

    init {
        this.refreshActionMap()
    }


    // Public Methods
    override fun read(json: Json, jsonData: JsonValue) {
        for (value in jsonData) {
            val action = GoblinInput.InputActions.valueOf(value.name)
            this.inputMap[action] = value.map { obj ->
                UserAction.fromJson(obj)
            }
        }

        this.refreshActionMap()
    }

    override fun write(json: Json) {
        for (pair in this.inputMap) {
            val (action, keys) = pair
            json.writeArrayStart(action.toString())
            for (key in keys) {
                json.writeValue(key)
            }
            json.writeArrayEnd()
        }
    }


    // Private Methods
    private fun refreshActionMap() {
        /*
        Flips the input map on its head.  Instead of pointing from action -> list of keys, it will point key -> list of actions
        E.G.
            Input Map
                LEFT -> {Keys.A, Keys.LEFT}
                RIGHT -> {Keys.D, Keys.RIGHT}
                ATTACK -> {Keys.K, Mouse.LEFT, Keys.A}

            Action Map
                Keys.A -> {LEFT, ATTACK}
                Keys.D -> {RIGHT}
                Keys.LEFT -> {LEFT}
                Keys.RIGHT -> {RIGHT}
                Keys.K -> {ATTACK}
                Mouse.LEFT -> {ATTACK}
         */
        this.actionMap = this.inputMap
                .flatMap { pair -> pair.value }
                .toMapBy(
                        { action -> action },
                        { action ->
                            this.inputMap
                                    .filter { pair -> pair.value.contains(action) }
                                    .map { pair -> pair.key }
                        }
                )

        // Filter Out Individual Maps for keys/buttons being pressed, held, and released
        this.pressActionMap = this.actionMap.filter { it.key.timing == UserAction.ActionTiming.PRESS }
        Logger.debug { "Mapped ${pressActionMap.size} actions on a key press." }
        this.holdActionMap = this.actionMap.filter { it.key.timing == UserAction.ActionTiming.HOLD }
        Logger.debug { "Mapped ${holdActionMap.size} actions on a key hold." }
        this.releaseActionMap = this.actionMap.filter { it.key.timing == UserAction.ActionTiming.RELEASE }
        Logger.debug { "Mapped ${releaseActionMap.size} actions on a key release." }
    }


    companion object {
        private val BINDINGS_KEY = "keybinds"
    }
}

sealed class UserAction(code: Int, timing: ActionTiming) : Json.Serializable {

    enum class ActionTiming {
        PRESS,
        HOLD,
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

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is UserAction) {
            return false
        }

        return other.javaClass == this.javaClass &&
                other.code == this.code &&
                other.timing == this.timing
    }

    override fun hashCode(): Int {
        var hash = this.javaClass.hashCode()
        hash = code + hash * 31
        hash = timing.hashCode() + hash * 31
        return hash
    }

    companion object {
        private val TYPE_KEY = "type"
        private val CODE_KEY = "code"
        private val TIMING_KEY = "timing"

        fun fromJson(jsonData: JsonValue): UserAction {
            if (jsonData.has(TYPE_KEY)) {
                var code = Input.Keys.ENTER
                var timing = ActionTiming.PRESS

                if (jsonData.has(CODE_KEY)) {
                    code = jsonData.getInt(CODE_KEY)
                }

                if (jsonData.has(TIMING_KEY)) {
                    timing = ActionTiming.valueOf(jsonData.getString(TIMING_KEY))
                }

                return when (jsonData.getString(TYPE_KEY)) {
                    KeyAction::class.java.simpleName -> KeyAction(code, timing)
                    MouseAction::class.java.simpleName -> MouseAction(code, timing)
                    else -> {
                        Logger.debug { "Could not find type for UserAction, using default action instead." }
                        return KeyAction()
                    }
                }
            } else {
                Logger.debug { "Could not find type for UserAction, using default action instead." }
                return KeyAction()
            }
        }
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