package com.github.ybecker.epforuml.sensor

import android.net.Uri

interface AudioPlayer {
    fun playFile(uri: Uri)
    fun stop()
}