package com.github.ybecker.epforuml.sensor

import android.net.Uri

/**
 * This represents an audio player
 */
interface AudioPlayer {
    fun playFile(uri: Uri)
    fun stop()
}
