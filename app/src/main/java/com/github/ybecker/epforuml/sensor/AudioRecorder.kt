package com.github.ybecker.epforuml.sensor

import java.io.File

/**
 * This represents an audio recorder.
 */
interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()


}