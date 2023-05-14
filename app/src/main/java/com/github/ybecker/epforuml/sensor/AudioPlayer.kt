package com.github.ybecker.epforuml.sensor

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)

    fun stop()
}