package com.github.ybecker.epforuml.sensor

import java.io.File

interface AudioRecorder {

    fun start(outputFile: File)
    fun stop()


}