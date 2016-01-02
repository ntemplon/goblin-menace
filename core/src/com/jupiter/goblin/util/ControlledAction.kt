package com.jupiter.goblin.util

import com.jupiter.goblin.io.Logger

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

/**
 * A class allowing control over when an action is invoked
 * @property interval The minimum amount of time that must elapse between subsequent instances of the action being
 * performed
 * @property action The action that will be performed
 * @property guards A set of conditions that must all be true for the action to be performed
 */
class ControlledAction(val interval: Float, val action: () -> Unit, val guards: Set<() -> Boolean> = setOf()) {

    private var started: Boolean = false
    private var lastTime: Long = 0L

    /**
     * Whether or not the current action is suspended
     */
    public var suspended: Boolean = false
        private set

    /**
     * Requests that the action be performed, and performs it if necessary
     * @return True if the action was executed, and false if it was not
     */
    fun request(): Boolean {
        // If suspended, do not execute the action
        if (suspended) {
            return false
        }

        // If any guards do not return true, then do not execute the action
        if (guards.any { guard -> !guard.invoke() }) {
            return false
        }

        // Get the current time, in nanoseconds
        val time = System.nanoTime()

        if (!started) {
            lastTime = time
            started = true
            action.invoke()
            return true
        } else {
            // Calculate elapsed time since the action was last performed, in seconds
            val elapsed = (time - lastTime) / 1e9f
            if (elapsed >= interval) {
                lastTime = time
                action.invoke()
                return true
            }
        }

        return false
    }

    /**
     * Suspends the action.  All request() calls will not perform the action until resume() is called.
     */
    fun suspend() {
        this.suspended = true
    }

    /**
     * Resumes the action from a suspended state.
     */
    fun resume() {
        this.suspended = false
    }

}