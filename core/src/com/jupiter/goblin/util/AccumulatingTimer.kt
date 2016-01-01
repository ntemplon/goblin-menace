package com.jupiter.goblin.util

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
 * A class for timing an integration function with a semi-fixed time step
 * @property interval The interval at which the integration function should be integrated, in seconds
 * @property integrator The function that performs the appropriate integration, given the elapsed time in seconds
 */
class AccumulatingTimer(interval: Float, val integrator: (Float) -> Unit) {

    // Properties
    private var accumulator: Float = 0.0f
    private var halfInterval = interval * 0.5f

    public var interval: Float = interval
        get() = field
        set(value) {
            field = value
            halfInterval = value * 0.5f
        }


    // Public Methods
    fun tick(frameTime: Float) {
        this.accumulator += frameTime
        while (this.accumulator >= halfInterval) {
            this.integrator.invoke(interval)
            this.accumulator -= interval
        }
    }

}