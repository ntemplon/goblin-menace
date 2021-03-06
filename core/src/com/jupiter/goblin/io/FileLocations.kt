package com.jupiter.goblin.io

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

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
object FileLocations {
    val RootFolder: FileHandle = FileHandle("./")
    val LogFile = RootFolder.child("log.txt")

    val ConfigFile: FileHandle = RootFolder.child("config")
    val SettingsFile: FileHandle = ConfigFile.child("settings.cfg")

    val AssetsFolder: FileHandle = RootFolder.child("assets")

    val FontFolder: FileHandle = AssetsFolder.child("font")

    val LevelFolder: FileHandle = AssetsFolder.child("level")
    val CastleFolder: FileHandle = LevelFolder.child("castle")


//    private fun findAssetsFolder(): FileHandle {
//        val externalFolder = RootFolder.child("assets")
//        if (externalFolder.exists()) {
//            Logger.info { "Using external assets folder." }
//            return externalFolder
//        }
//
//        val internalFolder = Gdx.files.internal("assets")
//        if (internalFolder.exists()) {
//            Logger.info { "Using internal assets folder." }
//            return internalFolder
//        }
//
//        throw IllegalStateException("Cannot find an assets folder!")
//    }
}