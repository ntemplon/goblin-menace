package com.jupiter.goblin

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.jupiter.ganymede.event.Event
import com.jupiter.ganymede.event.EventWrapper
import com.jupiter.goblin.entity.PhysicsBindingSystem
import com.jupiter.goblin.entity.PhysicsSystem
import com.jupiter.goblin.input.Controllers
import com.jupiter.goblin.input.GoblinInput
import com.jupiter.goblin.io.FileLocations
import com.jupiter.goblin.io.GoblinAssetManager
import com.jupiter.goblin.io.JsonSerializer
import com.jupiter.goblin.io.Logger
import com.jupiter.goblin.util.silentDispose

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

    /**
     * The operating priority of the physics system (lower goes first)
     */
    val PhysicsSystemPriority = 1000
    /**
     * The operating priority of the physics binding system (lower goes first)
     */
    val PhysicsBindingSystemPriority = 1100


    // Immutable Properties
    /**
     * The backing for the event that is dispatched when a fatal error occurs
     */
    private val fatalErrorEvent = Event<Throwable>()
    /**
     * An event that is dispatched when a fatal error occurs. It is up to subscribers to this event to close the game.
     */
    val fatalError = EventWrapper(fatalErrorEvent)
    /**
     * The entity engine powering the entities in the game
     */
    val entityEngine = Engine().apply {
        addSystem(PhysicsSystem)
        addSystem(PhysicsBindingSystem)
    }


    // Mutable Properties
    /**
     * The currently displayed screen for this game.  This calls the getScreen() and setScreen()
     * methods of the superclass, unlike the screen field/property
     */
    public var currentScreen: Screen?
        get() = this.getScreen()
        set(value) = this.setScreen(value)

    /**
     * The currently used settings for the game.  Setting this property should enact any changes
     * required to bring the game up to date.
     */
    public var settings: Settings = this.readSettings()
        get() = field
        private set(value) {
            field = value
        }


    // Game Methods
    /**
     * Initializes the various aspects of the game, and starts the loading screen
     */
    override fun create() {
        try {
            LoadingScreen.finishedLoading.addListener { goToGameScreen() }
            this.currentScreen = LoadingScreen
            Gdx.input.inputProcessor = GoblinInput
            com.badlogic.gdx.controllers.Controllers.addListener(object: ControllerAdapter() {
                override fun connected(controller: Controller?) {
                    controller?.addListener(Controllers)
                }
                override fun disconnected(controller: Controller?) {
                    controller?.removeListener(Controllers)
                }
            })
        } catch (ex: Exception) {
            this.fatalErrorEvent.dispatch(ex)
        }
    }

    /**
     * Renders the game
     */
    override fun render() {
        try {
            super.render()
        } catch (ex: Throwable) {
            this.fatalErrorEvent.dispatch(ex)
        }
    }

    /**
     * Disposes of any resources used by the game
     */
    override fun dispose() {
        try {
            GameScreen.silentDispose()
            PhysicsSystem.silentDispose()
            GoblinAssetManager.silentDispose()
        } catch (ex: Exception) {
            Logger.fatal { "Could not finish disposing all resources: Fatal Error Encountered." }
            this.fatalErrorEvent.dispatch(ex)
        }
    }

    // Public Methods
    /**
     * Shuts down the game, in preparation for application exit
     */
    fun shutdown() {
        this.writeSettings()
    }


    // Private Methods
    /**
     * Reloads the settings from [FileLocations].SettingsFile
     */
    private fun reloadSettings() {
        this.settings = this.readSettings()
    }

    /**
     * Returns the settings located at [FileLocations].SettingsFile
     */
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

    /**
     * Writes the current settings to [FileLocations].SettingsFile
     */
    private fun writeSettings() {
        Logger.info { "Writing settings file." }
        JsonSerializer.write(this.settings, FileLocations.SettingsFile)
    }

    private fun goToGameScreen() {
        currentScreen = GameScreen
    }

}