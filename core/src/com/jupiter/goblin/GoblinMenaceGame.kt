package com.jupiter.goblin

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.jupiter.ganymede.event.Event
import com.jupiter.ganymede.event.EventWrapper
import com.jupiter.goblin.entity.PhysicsBindingSystem
import com.jupiter.goblin.entity.PhysicsSystem
import com.jupiter.goblin.io.FileLocations
import com.jupiter.goblin.io.GoblinAssetManager
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
 * A singleton class representing the game
 */
public object GoblinMenaceGame : Game() {

    // Constants
    /**
     * The minimum width of the game window, in pixels
     */
    val MinWidth: Int = 1024
    /**
     * @property MinHeight the minimum height of the game window, in pixels
     */
    val MinHeight: Int = 720

    val PhysicsSystemPriority = 0
    val PhysicsBindingSystemPriority = 100
    val RenderingSystemPriority = 1000


    // Immutable Properties
    private val fatalErrorEvent = Event<Throwable>()

    /**
     * An event that is dispatched when a fatal error occurs. It is up to subscribers to this event to close the game.
     */
    val fatalError = EventWrapper(fatalErrorEvent)

    val entityEngine = Engine().apply {
        addSystem(PhysicsSystem)
        addSystem(PhysicsBindingSystem)
    }


    // Mutable Properties
    /**
     * @property currentScreen the currently displayed screen for this game.  This calls the getScreen() and setScreen()
     * methods of the superclass, unlike the screen field/property
     */
    public var currentScreen: Screen?
        get() = this.getScreen()
        set(value) = this.setScreen(value)

    /**
     * @property settings the currently used settings for the game.  Setting this property should enact any changes
     * required to bring the game up to date.
     */
    public var settings: Settings = this.readSettings()
        get() = field
        private set(value) {
            field = value
        }


    // Game Methods
    override fun create() {
        try {
            //            GoblinAssetManager.load()
            //            GoblinAssetManager.finishLoading()
            //            this.currentScreen = GameScreen
            this.currentScreen = LoadingScreen.apply {
                finishedLoading.addListener { currentScreen = GameScreen }
            }
        } catch (ex: Exception) {
            this.fatalErrorEvent.dispatch(ex)
        }
    }

    override fun render() {
        try {
            super.render()
        } catch (ex: Throwable) {
            this.fatalErrorEvent.dispatch(ex)
        }
    }


    override fun dispose() {
        try {
            GameScreen.dispose()
            PhysicsSystem.dispose()
            GoblinAssetManager.dispose()
        } catch (ex: Exception) {
            Logger.fatal { "Could not finish disposing all resources: Fatal Error Encountered." }
            this.fatalErrorEvent.dispatch(ex)
        }
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
        return if (FileLocations.SettingsFile.exists() && !FileLocations.SettingsFile.isDirectory) {
            try {
                JsonSerializer.read<Settings>(FileLocations.SettingsFile)
            } catch (ex: Exception) {
                Logger.error(ex)
                Logger.info { "Using default settings." }
                Settings.default()
            }
        } else {
            Logger.info { "Could not find settings file; using default settings instead." }
            Settings.default()
        }
    }

    private fun writeSettings() {
        Logger.info { "Writing settings file." }
        JsonSerializer.write(this.settings, FileLocations.SettingsFile)
    }

}