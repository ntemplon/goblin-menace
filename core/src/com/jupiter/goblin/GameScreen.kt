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
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.jupiter.goblin.entity.*
import com.jupiter.goblin.io.FileLocations
import com.jupiter.goblin.io.GoblinAssetManager
import com.jupiter.goblin.io.Logger
import com.jupiter.goblin.level.RoomTemplate
import com.jupiter.goblin.util.addAll
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
public object GameScreen : Screen, Disposable {

    // Constants
    private val FPS_PADDING_RIGHT: Float = 5.0f
    private val FPS_PADDING_TOP: Float = 5.0f
    private val USAGE_INTERVAL: Float = 1.0f


    // Immutable Properties
    private val batch: Batch = SpriteBatch()
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GoblinMenaceGame.MinWidth.toFloat(), GoblinMenaceGame.MinHeight.toFloat(), this.camera)


    // Text Rendering
    private val textBatch: Batch = SpriteBatch()
    private val textCamera: Camera = OrthographicCamera()
    private val textViewport = FitViewport(GoblinMenaceGame.MinWidth.toFloat(), GoblinMenaceGame.MinHeight.toFloat(), this.textCamera)
    private val infoFont = GoblinAssetManager.get(FileLocations.FontFolder.child("Arial16.fnt").toString(), BitmapFont::class.java)
    private val infoLayout = GlyphLayout()
    private var usageAccumulator = 0.0f
    private var usageString = ""
    private val usageFormat = DecimalFormat ("0.0")


    // Physics
    private val physicsRenderer = Box2DDebugRenderer()


    // Rendering
    private val renderables: com.badlogic.ashley.utils.ImmutableArray<Entity> = GoblinMenaceGame.entityEngine.getEntitiesFor(Families.Renderables)

    /**
     * The controller used to determine where this screen should look each frame
     */
    var cameraController: CameraController? = null

    // Current hardcoded things - NOT FINAL
    private val room = RoomTemplate(GoblinAssetManager.get(FileLocations.CastleFolder.child("rooms").child("entrance.tmx").toString(), TiledMap::class.java))
    /**
     * Initializes relevant variables and prepares the screen to be shown
     */
    override fun show() {
        val render = RenderComponent(Sprite(GoblinAssetManager.get(AssetDescriptor(FileLocations.AssetsFolder.child("badlogic.jpg"), Texture::class.java))), 0.25f)

        val physComp = PhysicsSystem.polygon {
            body {
                type = BodyDef.BodyType.DynamicBody
                position.set(render.sprite.width / 2.0f, 20f)
            }

            shape {
                fitToSprite(render.sprite)
            }

            fixture {
                density = 0.1f
                friction = 0.2f
            }
        }

        val testEntity = Entity()
        testEntity.apply {
            add(render)
            add(physComp)
            add(PhysicsBindingComponent())
            //            add(FrameFunctionComponent().apply {
            //                add { ent, dt -> Mappers.Physics[ent].body.applyForceToCenter(Vector2(0.0f, 55f), true) }
            //            })
        }

        GoblinMenaceGame.entityEngine.addEntity(testEntity)
        GoblinMenaceGame.entityEngine.addAll(room.statics)

        this.cameraController = physComp.lockToCenter()
        //        this.camera.position.set(15.0f, 0.0f, 0.0f)
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
        val scale = GoblinMenaceGame.settings.renderScale
        this.viewport.setWorldSize(width.toFloat() / PhysicsSystem.PIXELS_PER_METER / scale, height.toFloat() / PhysicsSystem.PIXELS_PER_METER / scale)
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

        processInput()

        GoblinMenaceGame.entityEngine.update(delta)

        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        this.cameraController?.setPosition(this.camera)
        this.camera.update()
        this.batch.projectionMatrix = this.camera.combined

        this.room.renderer.setView(this.camera)
        this.room.renderer.renderBackground()

        this.batch.begin()
        for (ent in renderables) {
            Mappers.Render[ent].sprite.draw(this.batch)
        }
        this.batch.end()

        this.room.renderer.renderForeground()

        // Show Physics Debug, if applicable
        if (GoblinMenaceGame.settings.debugPhysics) {
            physicsRenderer.render(PhysicsSystem.world, camera.combined)
        }

        // Frame Time Saturation
        val elapsed = (System.nanoTime() - start) / 1e9
        val frameUsage = elapsed / delta
        if (frameUsage >= 1.0f) {
            Logger.info { "Frame usage time exceeded allowed. Allowed: $delta. Taken: $elapsed." }
        }

        // Show FPS, if applicable
        if (GoblinMenaceGame.settings.showFps) {
            this.textCamera.update()
            this.textBatch.projectionMatrix = this.textCamera.combined

            this.textBatch.begin()

            val fpsString = "FPS: " + Gdx.graphics.framesPerSecond.toString()
            this.infoLayout.setText(this.infoFont, fpsString)
            // Provided coordinates are top-left of the text
            this.infoFont.draw(this.textBatch, fpsString,
                    0.5f * this.textViewport.worldWidth - this.infoLayout.width - FPS_PADDING_RIGHT + this.textCamera.position.x,
                    0.5f * this.textViewport.worldHeight - FPS_PADDING_TOP + this.textCamera.position.y)

            this.usageAccumulator += delta

            if (usageAccumulator >= USAGE_INTERVAL) {
                this.usageString = "Usage: " + usageFormat.format(frameUsage * 100.0) + "%"
                usageAccumulator -= USAGE_INTERVAL
            }

            this.infoLayout.setText(this.infoFont, this.usageString)

            this.infoFont.draw(this.textBatch, this.usageString,
                    0.5f * this.textViewport.worldWidth - this.infoLayout.width - FPS_PADDING_RIGHT + this.textCamera.position.x,
                    0.5f * this.textViewport.worldHeight - 2 * FPS_PADDING_TOP + this.textCamera.position.y - this.infoLayout.height)

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
        this.room.dispose()
    }


    // Private Methods
    private fun processInput() {

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