package com.jupiter.goblin.io

import com.badlogic.gdx.files.FileHandle
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.collections.joinToString
import kotlin.collections.map

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
object Logger {

    enum class LoggingLevel(public val severity: Int) {
        DEBUG(100),
        INFO(200),
        WARN(300),
        ERROR(400),
        FATAL(500)
    }

    // Constants
    val DefaultLoggingLevel: LoggingLevel = LoggingLevel.DEBUG

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")


    // Mutable Properties
    public var loggingLevel: LoggingLevel = DefaultLoggingLevel
        get() = field
        set(value) {
            if (field == value) {
                return
            }

            field = value
            info { "Setting logging level to ${value.toString()}" }
        }

    private var task: LoggingTask? = null
    private var thread: Thread? = null


    // Public Methods
    fun open(file: FileHandle) {
        this.close()
        this.task = LoggingTask(file)
        val thread = Thread(this.task, "Thread for log at \"${file.path()}\"")
        this.thread = thread
        thread.start()
    }

    fun close() {
        this.task?.stop()
    }

    fun log(message: () -> String, level: LoggingLevel) {
        if (level.severity >= this.loggingLevel.severity) {
            this.task?.enqueue("[${level.toString()} ${LocalTime.now().format(this.formatter)}] ${message.invoke()}")
        }
    }

    fun debug(message: () -> String) = this.log(message, LoggingLevel.DEBUG)
    fun info(message: () -> String) = this.log(message, LoggingLevel.INFO)
    fun warn(message: () -> String) = this.log(message, LoggingLevel.WARN)
    fun error(message: () -> String) = this.log(message, LoggingLevel.ERROR)
    fun fatal(message: () -> String) = this.log(message, LoggingLevel.FATAL)

    fun log(ex: Throwable, level: LoggingLevel) {
        log({
            "${ex.javaClass.typeName}: ${ex.message}" + System.lineSeparator() +
                    ex.stackTrace.map { "\t" + it.toString() }
                            .joinToString(separator = System.lineSeparator())
        }, level)
    }

    fun debug(ex: Throwable) = this.log(ex, LoggingLevel.DEBUG)
    fun info(ex: Throwable) = this.log(ex, LoggingLevel.INFO)
    fun warn(ex: Throwable) = this.log(ex, LoggingLevel.WARN)
    fun error(ex: Throwable) = this.log(ex, LoggingLevel.ERROR)
    fun fatal(ex: Throwable) = this.log(ex, LoggingLevel.FATAL)

    fun join() = this.thread?.join()

    fun flush() = this.task?.flush()


    object GdxAdapter : com.badlogic.gdx.utils.Logger("") {

        override fun debug(message: String?) {
            if (message != null) Logger.debug { message }
        }

        override fun debug(message: String?, exception: Exception?) {
            if (exception != null) Logger.debug(exception)
        }

        override fun info(message: String?) {
            if (message != null) Logger.info { message }
        }

        override fun info(message: String?, exception: Exception?) {
            if (exception != null) Logger.info(exception)
        }

        override fun error(message: String?) {
            if (message != null) Logger.error { message }
        }

        override fun error(message: String?, exception: Throwable?) {
            if (exception != null) Logger.error(exception)
        }

    }

}

private class LoggingTask(val file: FileHandle) : Runnable {

    // Immutable Properties
    val writer = file.writer(false) // false -> do not append
    private val queue = LinkedBlockingQueue<String>()


    // Mutable Properties
    private var stopped = false


    // Runnable Implementation
    override fun run() {
        writer.write("Starting Log File at ${LocalDateTime.now().format(FullFormat)}" + System.lineSeparator() +
                "Logging Level: ${Logger.loggingLevel.toString()}" + System.lineSeparator() +
                System.lineSeparator())

        while (!stopped) {
            // Specify String? because poll() will return null if nothing is available in the time
            val nextMessage: String? = queue.poll(WaitTime, WaitUnit)
            if (nextMessage != null) {
                writer.write(nextMessage + System.lineSeparator())
            }
        }

        this.writer.write(System.lineSeparator() + "Closing Goblin Menace Log File at ${LocalDateTime.now().format(FullFormat)}")
        this.writer.close()
    }


    // Public Methods
    public fun enqueue(message: String) {
        queue.put(message)
    }

    public fun stop() {
        this.stopped = true
    }

    fun flush() {
        this.writer.flush()
    }


    companion object {
        val WaitTime: Long = 1000
        val WaitUnit = TimeUnit.MILLISECONDS

        val FullFormat = DateTimeFormatter.ofPattern("MM/dd/YYYY HH:mm:ss")
    }

}