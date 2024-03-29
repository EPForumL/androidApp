package com.github.ybecker.epforuml.features.voiceMessages

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

class AndroidAudioPlayer(private val context: Context): AudioPlayer {
    private  var player: MediaPlayer? = null

    override fun playFile(uri: Uri) {
        //toUri: from where we want to play this file
        MediaPlayer.create(context, uri).apply {
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