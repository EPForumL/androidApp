package com.github.ybecker.epforuml.sensor

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File

class AndroidAudioPlayer(private val context: Context): AudioPlayer{
    private  var player: MediaPlayer? = null

    override fun playFile(file: File) {
        //toUri: from where we want to play this file
        MediaPlayer.create(context, file.toUri()).apply {
            player = this
            start()
        }
    }

    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }

}