package com.jupiter.goblin

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.jupiter.ganymede.event.Event
import com.jupiter.ganymede.event.EventWrapper
import com.jupiter.goblin.io.GoblinAssetManager
import java.text.DecimalFormat

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
object LoadingScreen : Screen {

    // Immutable Properties
    private val finishedLoadingEvent = Event<AssetManager>()
    val finishedLoading = EventWrapper(finishedLoadingEvent)

    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private val decFormat = DecimalFormat("0.#")
    private val camera = OrthographicCamera()


    override fun show() {
        GoblinAssetManager.load()
    }

    override fun pause() {

    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
    }

    override fun hide() {

    }

    override fun render(delta: Float) {
        GoblinAssetManager.update()

        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        this.camera.update()
        this.batch.projectionMatrix = this.camera.combined

        val progress = GoblinAssetManager.progress
        val message = "Loading: ${decFormat.format(progress * 100f)}%"
        layout.setText(font, message)

        // (0, 0) is in the center of the screen
        // (x, y) is the top-left corner of the text
        val x = layout.width * -0.5f
        val y = layout.height * 0.5f

        batch.begin()
        font.draw(this.batch, layout, x, y)
        batch.end()

        if (progress >= 1.0f) {
            this.finishedLoadingEvent.dispatch(GoblinAssetManager)
        }
    }

    override fun resume() {

    }

    override fun dispose() {

    }

}