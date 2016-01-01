package com.jupiter.goblin.util

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
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
fun <T : com.badlogic.gdx.utils.Disposable> T.using(action: (T) -> Unit) {
    action(this)
    this.dispose()
}

fun com.badlogic.gdx.utils.Disposable.silentDispose() {
    try {
        this.dispose()
    } catch (ex: Throwable) {
        Logger.error(ex)
    }
}

fun <T> T.with(action: T.() -> Unit) {
    this.action()
}

fun Engine.addAll(entities: Iterable<Entity>) {
    for (entity in entities) {
        this.addEntity(entity)
    }
}