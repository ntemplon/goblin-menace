package com.jupiter.goblin

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
public class Settings private constructor() : Json.Serializable {

    public var showFps: Boolean = false
    public var useVsync: Boolean = true
    public var debugPhysics: Boolean = false

    public var logLevel: Logger.LoggingLevel = Logger.LoggingLevel.WARN

    override fun write(json: Json) {
        json.writeValue(LogLevelKey, this.logLevel.toString())
        json.writeValue(DebugPhysicsKey, this.debugPhysics)
        json.writeValue(DisplayFpsKey, this.showFps)
        json.writeValue(UseVsyncKey, this.useVsync)
    }

    override fun read(json: Json, jsonData: JsonValue) {
        if (jsonData.has(DisplayFpsKey)) {
            try {
                this.showFps = jsonData.getBoolean(DisplayFpsKey)
            } catch (ex: Exception) {
                Logger.warn(ex)
                Logger.warn { "Error while parsing value for key \"$DisplayFpsKey.\" Using default value of \"${this.showFps}\"" }
            }
        } else {
            Logger.debug { "Could not find key \"$DisplayFpsKey\" in the settings file." }
        }

        if (jsonData.has(UseVsyncKey)) {
            try {
                this.useVsync = jsonData.getBoolean(UseVsyncKey)
            } catch (ex: Exception) {
                Logger.warn(ex)
                Logger.warn { "Error while parsing value for key \"$UseVsyncKey.\" Using default value of \"${this.useVsync}\"" }
            }
        } else {
            Logger.debug { "Could not find key \"$UseVsyncKey\" in the settings file." }
        }

        if (jsonData.has(LogLevelKey)) {
            try {
                this.logLevel = Logger.LoggingLevel.valueOf(jsonData.getString(LogLevelKey))
            } catch (ex: Exception) {
                Logger.warn(ex)
                Logger.warn { "Error while parsing value for key \"$LogLevelKey.\" Using default value of \"${this.logLevel.toString()}\"" }
            }
        } else {
            Logger.debug { "Could not find key \"$LogLevelKey\" in the settings file." }
        }

        if (jsonData.has(DebugPhysicsKey)) {
            try {
                this.debugPhysics = jsonData.getBoolean(DebugPhysicsKey)
            } catch (ex: Exception) {
                Logger.warn(ex)
                Logger.warn { "Error while parsing value for key \"$DebugPhysicsKey.\" Using default value of \"${this.debugPhysics}\"" }
            }
        } else {
            Logger.debug { "Could not find key \"$DebugPhysicsKey\" in the settings file." }
        }
    }

    companion object {
        val LogLevelKey = "loglevel"
        val DebugPhysicsKey = "physdebug"
        val DisplayFpsKey = "showfps"
        val UseVsyncKey = "vsync"

        fun default(): Settings = Settings()
    }

}