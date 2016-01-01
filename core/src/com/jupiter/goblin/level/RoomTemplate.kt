package com.jupiter.goblin.level

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Disposable
import com.jupiter.goblin.entity.PhysicsSystem
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
 * A class representing a template for a room, i.e., a room with no state
 */
class RoomTemplate(val map: TiledMap) : Disposable {

    val backgrounds: List<TiledMapTileLayer> = map.layers
            .map { it as? TiledMapTileLayer }
            .filterNotNull()
            .filter { it.name.startsWith(BACKGROUND_IDENTIFIER) && it is TiledMapTileLayer }
            .sortedBy { it.name.replace(BACKGROUND_IDENTIFIER, "").toInt() }
    val foregrounds: List<TiledMapTileLayer> = map.layers
            .map { it as? TiledMapTileLayer }
            .filterNotNull()
            .filter { it.name.startsWith(FOREGROUND_IDENTIFIER) }
            .sortedBy { it.name.replace(FOREGROUND_IDENTIFIER, "").toInt() }
    val collision: MapLayer = map.layers
            .firstOrNull { it.name == COLLISION_IDENTIFIER }
            ?: MapLayer()

    val statics: Set<Entity> by lazy {
        this.collision.objects
                .flatMap {
                    when (it) {
                        is PolylineMapObject -> it.toEdgeEntities()
                        is RectangleMapObject -> listOf(it.toRectangleEntity())
                        else -> listOf<Entity>()
                    }
                }
                .toSet()
    }

    val backgroundIndices: IntArray = IntArray(backgrounds.size, { i -> map.layers.indexOf(backgrounds[i]) })
    val foregroundIndices: IntArray = IntArray(foregrounds.size, { i -> map.layers.indexOf(foregrounds[i]) })

    val renderer: RoomRenderer by lazy { RoomRendererImpl(this) }


    // Public Methods
    public override fun dispose() {
        this.renderer.dispose()
    }


    // Private Functions
    private fun PolylineMapObject.toEdgeEntities(): List<Entity> {
        return listOf<Entity>()
    }

    private fun RectangleMapObject.toRectangleEntity(): Entity {
        val obj = this
        val rectangle = obj.rectangle
        return Entity().apply {
            add(PhysicsSystem.polygon {
                body {
                    type = BodyDef.BodyType.StaticBody
                    position.set(rectangle.x, rectangle.y)
                }

                shape {
                    setAsBox(rectangle.width / 2.0f, rectangle.height / 2.0f)
                }

                fixture {

                }
            })
        }
    }

    companion object {
        val BACKGROUND_IDENTIFIER: String = "background"
        val FOREGROUND_IDENTIFIER: String = "foreground"
        val COLLISION_IDENTIFIER: String = "collision"
    }

}

private class RoomRendererImpl(val room: RoomTemplate) : OrthogonalTiledMapRenderer(room.map, PhysicsSystem.METERS_PER_PIXEL), RoomRenderer {

    override fun renderBackground() {
//        super.render(room.backgroundIndices)
        beginRender()
        for(backLayer in room.backgrounds) {
            super.renderTileLayer(backLayer)
        }
        endRender()
    }

    override fun renderForeground() {
        super.render(room.foregroundIndices)
    }

}

interface RoomRenderer : TiledMapRenderer, Disposable {
    fun renderBackground()
    fun renderForeground()
}