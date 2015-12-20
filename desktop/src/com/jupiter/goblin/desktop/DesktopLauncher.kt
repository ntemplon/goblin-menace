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
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
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
    Logger.open(FileLocations.LOG_FILE)

    val game = GoblinMenaceGame

    Logger.loggingLevel = game.settings.logLevel

    val config = LwjglApplicationConfiguration().apply {
        title = WindowTitle
        resizable = Resizable
        vSyncEnabled = game.settings.useVsync
    }

    val frame = LwjglFrame(game, config).apply {
        minimumSize = Dimension(GoblinMenaceGame.MIN_WIDTH, GoblinMenaceGame.MIN_HEIGHT)
        extendedState = extendedState or JFrame.MAXIMIZED_BOTH // Bitwise or -> do both
        defaultCloseOperation = JFrame.HIDE_ON_CLOSE

        // Handle the window closing
//        addWindowListener(DesktopWindowListener)

        // Handle the window hiding event that happens on close
        addComponentListener(DesktopComponentListener)
    }
}

/**
 * @property GameName The name of the game
 */
val GameName: String = "Goblin Menace"

/**
 * @property MajorVersion The major version number of this release of the game
 */
val MajorVersion: Int = 0

/**
 * @property MinorVersion The minor version number of this release of the game
 */
val MinorVersion: Int = 0

/**
 * @property Revision The revision number of this release of the game
 */
val Revision: Int = 1

/**
 * @property WindowTitle The title for the window in which the game will be shown
 */
val WindowTitle: String = "$GameName v$MajorVersion.$MinorVersion.$Revision"

/**
 * @property Resizable If the game window should be resizable
 */
val Resizable: Boolean = true

object DesktopWindowListener: WindowAdapter() {
    override fun windowStateChanged(e: WindowEvent) {
        // We'll dispose of it later, but make it invisible for now
//        e.window.isVisible = false

//        // Save Games and Such
//        GoblinMenaceGame.shutdown()
//
//        // Actually close everything down
//        Logger.close()
//        Logger.join()
//        Gdx.app.exit()
    }
}

/**
 * A singleton object to listen to component events coming from the main game window
 */
object DesktopComponentListener: ComponentAdapter() {
    /**
     * Closes the game when the main window is hidden.
     *
     * @param e The event currently being handled
     */
    override  fun componentHidden(e: ComponentEvent) {
        // Save Games and Such
        GoblinMenaceGame.shutdown()

        // Actually close everything down
        Logger.close()
        Logger.join()
        Gdx.app.exit()
    }
}