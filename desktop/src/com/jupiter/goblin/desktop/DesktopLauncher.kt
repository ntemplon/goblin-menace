package com.jupiter.goblin.desktop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglFrame
import com.jupiter.goblin.GoblinMenaceGame
import com.jupiter.goblin.io.FileLocations
import com.jupiter.goblin.io.Logger
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.util.*
import javax.swing.JFrame

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
 * Starts the game
 */
fun main(args: Array<String>) {
    var gl30ByArg = false
    var gl20ByArg = false

    for (arg: String in args.map { it.toLowerCase(Locale.US) }) {
        when (arg) {
            GL_30_ARG -> gl30ByArg = true
            GL_20_ARG -> gl20ByArg = true
        }
    }

    Logger.open(FileLocations.LogFile)

    val game = GoblinMenaceGame.apply {
        fatalError.addListener { ex ->
            Logger.fatal(ex)
            Logger.fatal { "Closing game due to unhandled exception." }
            closeGame()
        }
    }

    val config = LwjglApplicationConfiguration().apply {
        title = WINDOW_TITLE
        resizable = RESIZABLE
        vSyncEnabled = game.settings.useVsync
        allowSoftwareMode = false
        foregroundFPS = game.settings.targetFps
        fullscreen = false
        useGL30 = gl30ByArg && !gl20ByArg
    }

    // No need to store it because, honestly, we don't use it
    val frame = LwjglFrame(game, config).apply {
        minimumSize = Dimension(GoblinMenaceGame.MinWidth, GoblinMenaceGame.MinHeight)

        // Bitwise or -> do both
        extendedState = extendedState or JFrame.MAXIMIZED_BOTH

        defaultCloseOperation = JFrame.HIDE_ON_CLOSE

        // Handle the window hiding event (that happens on close)
        addComponentListener(DesktopComponentListener)
    }
}

/**
 * @property GAME_NAME The name of the game
 */
val GAME_NAME: String = "Goblin Menace"

/**
 * @property MAJOR_VERSION The major version number of this release of the game
 */
val MAJOR_VERSION: Int = 0

/**
 * @property MINOR_VERSION The minor version number of this release of the game
 */
val MINOR_VERSION: Int = 0

/**
 * @property REVISION The revision number of this release of the game
 */
val REVISION: Int = 1

/**
 * @property WINDOW_TITLE The title for the window in which the game will be shown
 */
val WINDOW_TITLE: String = "$GAME_NAME v$MAJOR_VERSION.$MINOR_VERSION.$REVISION"

/**
 * @property RESIZABLE If the game window should be resizable
 */
val RESIZABLE: Boolean = true

val GL_30_ARG = "-gl30"
val GL_20_ARG = "-gl20"

/**
 * Closes the game
 */
private fun closeGame() {
    try {
        // Save Games and Such
        GoblinMenaceGame.shutdown()

        // Actually close everything down
        Logger.close()
        Logger.join()
    } finally {
        Gdx.app.exit()
    }
}

/**
 * A singleton object to listen to component events coming from the main game window
 */
object DesktopComponentListener : ComponentAdapter() {
    /**
     * Closes the game when the main window is hidden.
     *
     * @param e The event currently being handled
     */
    override fun componentHidden(e: ComponentEvent) {
        Logger.info { "Closing game at user request." }
        closeGame()
    }
}