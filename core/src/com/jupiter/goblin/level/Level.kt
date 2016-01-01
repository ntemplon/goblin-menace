package com.jupiter.goblin.level

import com.badlogic.gdx.files.FileHandle

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
 * A class representing an individual level instance, i.e. the level after the player has killed enemies, picked up
 * items, etc.
 */
class Level private constructor() {
    companion object {
        //TODO: Implementation
        /**
         * Creates a level from a template with the initial state
         *
         * @param template the [LevelTemplate] to base this level off of
         */
        fun newLevel(template: LevelTemplate): Level {
            throw UnsupportedOperationException()
        }

        // TODO: Implementation
        /**
         * Creates a level from a template and a state
         *
         * @param template the [LevelTemplate] to base this level off of
         * @param state the state of the level
         */
        fun withState(template: LevelTemplate, state: LevelState): Level {
            throw UnsupportedOperationException()
        }
    }
}

/**
 * A class representing everything required to turn a LevelTemplate into the appropriate Level object
 */
class LevelState {

}