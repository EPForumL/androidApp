package com.github.ybecker.epforuml;

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.ybecker.epforuml.features.voiceMessages.AndroidAudioRecorder
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AndroidAudioRecorderTest {
    private lateinit var audioRecorder: AndroidAudioRecorder
    private lateinit var outputFile: File

    @get:Rule
    val permissionRule: GrantPermissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        audioRecorder = AndroidAudioRecorder(context)
        outputFile = File(context.filesDir, "test_audio.mp4")
    }

    @After
    fun tearDown() {
        outputFile.delete()
    }

    @Test
    fun testStartAndStopRecording() {
        audioRecorder.start(outputFile)

        // Wait for a while to simulate recording
        Thread.sleep(2000)

        audioRecorder.stop()

        assertTrue("Output file should exist", outputFile.exists())
        assertTrue("Output file should not be empty", outputFile.length() > 0)
    }
}
