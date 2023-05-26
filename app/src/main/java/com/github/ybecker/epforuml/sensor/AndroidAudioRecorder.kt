package com.github.ybecker.epforuml.sensor

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.FileOutputStream

class AndroidAudioRecorder(private  val context: Context): AudioRecorder {

    var recorder: MediaRecorder? = null


    private fun createRecorder(): MediaRecorder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return MediaRecorder(context)
        } else {
            return MediaRecorder()
        }
    }

    override fun start(outputFile: File) {
        //initialize recorder
        createRecorder().apply {
            //The audio source is the device's microphone
            setAudioSource(MediaRecorder.AudioSource.MIC)
            //The output format is MPEG-4
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            //The audio encoder is AAC
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            //The output file is the one we created
            setOutputFile(FileOutputStream(outputFile).fd)

            //Prepare the recorder
            prepare()
            //Start recording
            start()

            recorder = this


        }
    }


    override fun stop() {
        recorder?.stop()
        recorder?.reset()
        recorder= null
    }


}