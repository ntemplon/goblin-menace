package com.jupiter.goblin

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.jupiter.goblin.entity.*
import com.jupiter.goblin.io.FileLocations
import com.jupiter.goblin.io.GoblinAssetManager
import com.jupiter.goblin.io.Logger
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
public object GameScreen : Screen {

    // Constants
    private val FPS_PADDING_RIGHT: Float = 5.0f
    private val FPS_PADDING_TOP: Float = 5.0f
    private val USAGE_INTERVAL: Float = 1.0f


    // Immutable Properties
    private val batch: Batch = SpriteBatch()
    private val camera: Camera = OrthographicCamera()
    private val viewport = FitViewport(GoblinMenaceGame.MinWidth.toFloat(), GoblinMenaceGame.MinHeight.toFloat(), this.camera)


    // Text Rendering
    private val textBatch: Batch = SpriteBatch()
    private val textCamera: Camera = OrthographicCamera()
    private val textViewport = FitViewport(GoblinMenaceGame.MinWidth.toFloat(), GoblinMenaceGame.MinHeight.toFloat(), this.textCamera)
    private val fpsFont = GoblinAssetManager.get(FileLocations.FontFolder.child("Arial16.fnt").toString(), BitmapFont::class.java)
    private val fpsLayout = GlyphLayout()
    private var usageAccumulator = 0.0f
    private var usageString = ""
    private val usageFormat = DecimalFormat ("00.0")

    // Physics
    private val physicsRenderer = Box2DDebugRenderer()

    /**
     * The controller used to determine where this screen should look each frame
     */
    var cameraController: CameraController? = null

    // Current hardcoded things - NOT FINAL
    private val entity = Entity()

    /**
     * Initializes relevant variables and prepares the screen to be shown
     */
    override fun show() {
        val render = RenderComponent(Sprite(GoblinAssetManager.get(AssetDescriptor(FileLocations.AssetsFolder.child("badlogic.jpg"), Texture::class.java))))

        val physComp = PhysicsSystem.polygon {
            body {
                type = BodyDef.BodyType.DynamicBody
                position.set(0f, 0f)
            }

            shape {
                fitToSprite(render.sprite)
            }

            fixture {
                density = 1.0f
            }
        }

        val groundComp = PhysicsSystem.edge {
            body {
                type = BodyDef.BodyType.StaticBody
                position.set(0f, 0f)
            }

            shape {
                set(-50f, -20f, 50f, -20f)
            }

            fixture {

            }
        }

        this.entity.apply {
            add(render)
            add(physComp)
            add(PhysicsBindingComponent())
        }

        GoblinMenaceGame.entityEngine.addEntity(this.entity)

        this.cameraController = physComp.lockToCenter()
    }

    /**
     * No-op: is only called immediately before dispose()
     */
    override fun pause() {

    }

    /**
     * Handles the screen being set to another size
     * @param width the new screen width, in pixels
     * @param height the new screen height, in pixels
     */
    override fun resize(width: Int, height: Int) {
        this.viewport.setWorldSize(width.toFloat() / PhysicsSystem.PIXELS_PER_METER, height.toFloat() / PhysicsSystem.PIXELS_PER_METER)
        this.viewport.update(width, height)

        this.textViewport.setWorldSize(width.toFloat(), height.toFloat())
        this.textViewport.update(width, height)
    }

    /**
     * Prepares the screen to be hidden
     */
    override fun hide() {

    }

    /**
     * Renders the game
     * @param delta The elapsed time since the last render, in seconds
     */
    override fun render(delta: Float) {
        val start = System.nanoTime()
        GoblinMenaceGame.entityEngine.update(delta)

        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        this.cameraController?.setPosition(this.camera)
        this.camera.update()
        this.batch.projectionMatrix = this.camera.combined

        this.batch.begin()

        Mappers.Render[this.entity].sprite.draw(this.batch)

        //        val imgNow = this.img
        //        if (imgNow != null) {
        //            // Camera (0, 0) is located in the center of the screen
        //            // Position of image rendering is the bottom-left corner of the image
        //            this.batch.draw(imgNow, -0.5f * camera.viewportWidth, -0.5f * camera.viewportHeight)
        //        }

        this.batch.end()

        // Show Physics Debug, if applicable
        if (GoblinMenaceGame.settings.debugPhysics) {
            physicsRenderer.render(PhysicsSystem.world, camera.combined)
        }

        // Frame Time Saturation
        val elapsed = (System.nanoTime() - start) / 1e9
        val frameUsage = elapsed / delta
        if (frameUsage >= 1.0f) {
            Logger.warn { "Frame usage time exceeded allowed. Allowed: $delta. Taken: $elapsed." }
        }

        // Show FPS, if applicable
        if (GoblinMenaceGame.settings.showFps) {
            this.textCamera.update()
            this.textBatch.projectionMatrix = this.textCamera.combined

            this.textBatch.begin()

            val fpsString = "FPS: " + Gdx.graphics.framesPerSecond.toString()
            this.fpsLayout.setText(this.fpsFont, fpsString)
            // Provided coordinates are top-left of the text
            this.fpsFont.draw(this.textBatch, fpsString,
                    0.5f * this.textViewport.worldWidth - this.fpsLayout.width - FPS_PADDING_RIGHT + this.textCamera.position.x,
                    0.5f * this.textViewport.worldHeight - FPS_PADDING_TOP + this.textCamera.position.y)

            this.usageAccumulator += delta

            if (usageAccumulator >= USAGE_INTERVAL) {
                this.usageString = "Usage: " + usageFormat.format(frameUsage * 100.0) + "%"
                usageAccumulator -= USAGE_INTERVAL
            }

            this.fpsLayout.setText(this.fpsFont, this.usageString)

            this.fpsFont.draw(this.textBatch, this.usageString,
                    0.5f * this.textViewport.worldWidth - this.fpsLayout.width - FPS_PADDING_RIGHT + this.textCamera.position.x,
                    0.5f * this.textViewport.worldHeight - 2 * FPS_PADDING_TOP + this.textCamera.position.y - this.fpsLayout.height)

            this.textBatch.end()
        }
    }

    /**
     * Resumes the screen after being paused(): not relevant for a desktop-only app
     */
    override fun resume() {

    }

    /**
     * Disposes of any unmanaged resources used by the screen
     */
    override fun dispose() {
        this.batch.dispose()
        this.textBatch.dispose()
    }

}

/**
 * An interface for controllers that steer the camera for the GameScreen
 */
interface CameraController {
    /**
     * Sets the position of the provided camera
     */
    fun setPosition(camera: Camera)
}