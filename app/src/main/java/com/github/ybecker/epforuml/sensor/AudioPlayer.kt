package com.github.ybecker.epforuml.sensor

import java.net.URI

interface AudioPlayer {
    fun playFile(uri: URI)
    fun stop()
}