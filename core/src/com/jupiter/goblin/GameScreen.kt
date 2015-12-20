package com.jupiter.goblin

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.jupiter.goblin.io.FileLocations

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
public class GameScreen : Screen {

    companion object {
        // Constants
        private val FPS_PADDING_RIGHT: Float = 5.0f
        private val FPS_PADDING_TOP: Float = 5.0f
    }

    // Immutable Properties
    /**
     * @property batch The Batch that this screen renders with
     */
    private val batch: Batch = SpriteBatch()

    /**
     * @property camera The camera that controls how this screen looks at the world
     */
    private val camera: Camera = OrthographicCamera()

    private val viewport = FitViewport(GoblinMenaceGame.MIN_WIDTH.toFloat(), GoblinMenaceGame.MIN_HEIGHT.toFloat(), this.camera)

    private val logFont = BitmapFont()
    private val logLayout = GlyphLayout()


    // Mutable Properties
    private var img: Texture? = null

    override fun show() {
        this.img = Texture(FileLocations.ASSETS_FOLDER.child("badlogic.jpg"))
        this.viewport.apply()
    }

    override fun pause() {

    }

    override fun resize(width: Int, height: Int) {
        this.viewport.setWorldSize(width.toFloat(), height.toFloat())
        this.viewport.update(width, height)
    }

    override fun hide() {

    }

    /**
     * Renders the game
     * @param delta The elapsed time since the last render, in seconds
     */
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        this.camera.update()
        this.batch.projectionMatrix = this.camera.combined

        this.batch.begin()
        val imgNow = this.img
        if (imgNow != null) {
            // Camera (0, 0) is located in the center of the screen
            // Position of image rendering is the bottom-left corner of the image
            this.batch.draw(imgNow, -0.5f * camera.viewportWidth, -0.5f * camera.viewportHeight)
        }

        // Show FPS, if applicable
        if (GoblinMenaceGame.settings.showFps) {
            val fpsString = "FPS: " + Gdx.graphics.framesPerSecond
            this.logLayout.setText(this.logFont, fpsString)
            this.logFont.draw(this.batch, fpsString,
                    0.5f * this.viewport.worldWidth - this.logLayout.width - FPS_PADDING_RIGHT,
                    0.5f * this.viewport.worldHeight - FPS_PADDING_TOP) // Provided coordinates are top-left of the text
        }

        this.batch.end()
    }

    override fun resume() {

    }

    override fun dispose() {
        this.batch.dispose()
        this.img?.dispose()
    }

}