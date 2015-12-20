package com.jupiter.goblin

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.jupiter.goblin.io.FileLocations
import com.jupiter.goblin.io.JsonSerializer
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
 *
 */
public object GoblinMenaceGame : Game() {

    // Constants
    val MIN_WIDTH: Int = 1024
    val MIN_HEIGHT: Int = 720


    // Immutable Properties


    // Mutable Properties
    public var currentScreen: Screen?
        get() = this.getScreen()
        set(value) = this.setScreen(value)

    public var settings: Settings
        get
        private set

    private var _gameScreen: GameScreen? = null
    private val gameScreen: GameScreen
        get() = _gameScreen!!


    // Initialization
    init {
        this.settings = this.readSettings()
    }


    // Game Methods
    override fun create() {
        this._gameScreen = GameScreen()
        this.currentScreen = this.gameScreen
    }

    override fun render() {
        super.render()
    }

    // Due to logging stuff, make this a no-op
    override fun pause() {
    }

    override fun dispose() {
        this._gameScreen?.dispose()
    }


    // Public Methods
    fun shutdown() {
        this.writeSettings()
    }


    // Private Methods
    private fun reloadSettings() {
        this.settings = this.readSettings()
    }

    private fun readSettings(): Settings {
        Logger.info { "Reading settings file." }
        return if (FileLocations.SETTINGS_FILE.exists() && !FileLocations.SETTINGS_FILE.isDirectory) {
            JsonSerializer.read(FileLocations.SETTINGS_FILE)
        } else {
            Settings.default()
        }
    }

    private fun writeSettings() {
        Logger.info { "Writing settings file." }
        JsonSerializer.write(this.settings, FileLocations.SETTINGS_FILE)
    }

}