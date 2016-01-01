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

/**
 * A class representing settings for a Goblin Menace game.
 * NOTE:  Instances of this class are externally mutable, and events are not raised when properties are changed
 */
public class Settings private constructor() : Json.Serializable {

    /**
     * If the current FPS should be shown while playing
     */
    public var showFps: Boolean = false
    /**
     * If Vsync should be used while rendering
     */
    public var useVsync: Boolean = true
    /**
     * If the physics debug shapes should be rendered while playing
     */
    public var debugPhysics: Boolean = false
    /**
     * The level of log messages to be written
     */
    public var logLevel: Logger.LoggingLevel = Logger.LoggingLevel.WARN
    /**
     * The target FPS of the game when in the foreground
     */
    public var targetFps: Int = 60
    /**
     * The rate at which the physics of the game will be refreshed.  There is no interpolation used, so it is advisable
     * to keep this at the frame rate or higher.
     */
    public var physicsRefreshRate: Float = 60.0f
    /**
     * The linear scaling of the game (area will be scaled by this factor squared)
     */
    public var renderScale: Float = 1.0f


    // Public Methods
    /**
     * Serializes to the provided JSON output
     * @param json The writer to serialize to
     */
    override fun write(json: Json) {
        json.writeValue(LOG_LEVEL_KEY, this.logLevel.toString())
        json.writeValue(DEBUG_PHYSICS_KEY, this.debugPhysics)
        json.writeValue(PHYSICS_REFRESH_KEY, this.physicsRefreshRate)
        json.writeValue(RENDER_SCALE_KEY, this.renderScale)
        json.writeValue(DISPLAY_FPS_KEY, this.showFps)
        json.writeValue(TARGET_FPS_KEY, this.targetFps)
        json.writeValue(USE_VSYNC_KEY, this.useVsync)
    }

    /**
     * Deserializes the object using the provided json parser and data
     * @param json A JSON parser
     * @param jsonData The JSON data representing what this object should be
     */
    override fun read(json: Json, jsonData: JsonValue) {
        if (jsonData.has(DISPLAY_FPS_KEY)) {
            try {
                this.showFps = jsonData.getBoolean(DISPLAY_FPS_KEY)
            } catch (ex: Exception) {
                Logger.warn(ex)
                Logger.warn { "Error while parsing value for key \"$DISPLAY_FPS_KEY.\" Using default value of \"${this.showFps}\"" }
            }
        } else {
            Logger.debug { "Could not find key \"$DISPLAY_FPS_KEY\" in the settings file." }
        }

        if (jsonData.has(USE_VSYNC_KEY)) {
            try {
                this.useVsync = jsonData.getBoolean(USE_VSYNC_KEY)
            } catch (ex: Exception) {
                Logger.warn(ex)
                Logger.warn { "Error while parsing value for key \"$USE_VSYNC_KEY.\" Using default value of \"${this.useVsync}\"" }
            }
        } else {
            Logger.debug { "Could not find key \"$USE_VSYNC_KEY\" in the settings file." }
        }

        if (jsonData.has(LOG_LEVEL_KEY)) {
            try {
                this.logLevel = Logger.LoggingLevel.valueOf(jsonData.getString(LOG_LEVEL_KEY))
            } catch (ex: Exception) {
                Logger.warn(ex)
                Logger.warn { "Error while parsing value for key \"$LOG_LEVEL_KEY.\" Using default value of \"${this.logLevel.toString()}\"" }
            }
        } else {
            Logger.debug { "Could not find key \"$LOG_LEVEL_KEY\" in the settings file." }
        }

        if (jsonData.has(DEBUG_PHYSICS_KEY)) {
            try {
                this.debugPhysics = jsonData.getBoolean(DEBUG_PHYSICS_KEY)
            } catch (ex: Exception) {
                Logger.warn(ex)
                Logger.warn { "Error while parsing value for key \"$DEBUG_PHYSICS_KEY.\" Using default value of \"${this.debugPhysics}\"" }
            }
        } else {
            Logger.debug { "Could not find key \"$DEBUG_PHYSICS_KEY\" in the settings file." }
        }

        if (jsonData.has(TARGET_FPS_KEY)) {
            try {
                this.targetFps = jsonData.getInt(TARGET_FPS_KEY)
            } catch (ex: Exception) {
                Logger.warn(ex)
                Logger.warn { "Error while parsing value for key \"$TARGET_FPS_KEY.\" Using default value of \"${this.targetFps}\"" }
            }
        } else {
            Logger.debug { "Could not find key \"$TARGET_FPS_KEY\" in the settings file." }
        }

        if (jsonData.has(RENDER_SCALE_KEY)) {
            try {
                this.renderScale = jsonData.getFloat(RENDER_SCALE_KEY)
            } catch (ex: Exception) {
                Logger.warn(ex)
                Logger.warn { "Error while parsing value for key \"$RENDER_SCALE_KEY.\" Using default value of \"${this.renderScale}\"" }
            }
        } else {
            Logger.debug { "Could not find key \"$RENDER_SCALE_KEY\" in the settings file." }
        }

        if (jsonData.has(PHYSICS_REFRESH_KEY)) {
            try {
                this.physicsRefreshRate = jsonData.getFloat(PHYSICS_REFRESH_KEY)
            } catch (ex: Exception) {
                Logger.warn(ex)
                Logger.warn { "Error while parsing value for key \"$PHYSICS_REFRESH_KEY.\" Using default value of \"${this.physicsRefreshRate}\"" }
            }
        } else {
            Logger.debug { "Could not find key \"$PHYSICS_REFRESH_KEY\" in the settings file." }
        }
    }

    companion object {
        private val LOG_LEVEL_KEY = "loglevel"
        private val DEBUG_PHYSICS_KEY = "physdebug"
        private val PHYSICS_REFRESH_KEY = "physrate"
        private val RENDER_SCALE_KEY = "renderscale"
        private val DISPLAY_FPS_KEY = "showfps"
        private val TARGET_FPS_KEY = "targetfps"
        private val USE_VSYNC_KEY = "vsync"

        /**
         * Creates a new [Settings] instance with all of the default values populated.
         */
        fun default(): Settings = Settings()
    }

}