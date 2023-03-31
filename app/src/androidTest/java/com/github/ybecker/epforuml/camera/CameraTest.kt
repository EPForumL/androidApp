package com.github.ybecker.epforuml.camera

import android.app.Activity
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.CameraActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraTest {

    @Test
    fun testCaptureButtonVisible() {
        val intent = Intent(ApplicationProvider.getApplicationContext(),CameraActivity::class.java)

        val scenario = ActivityScenario.launch<Activity>(intent)

        // Check if the capture button is visible
        onView(withId(com.github.ybecker.epforuml.R.id.image_capture_button)).check(matches(isDisplayed()))

        // Check if the preview is visible
        onView(withId(com.github.ybecker.epforuml.R.id.viewFinder)).check(matches(isDisplayed()))

        scenario.close()

    }
}
