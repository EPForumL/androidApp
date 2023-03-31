package com.github.ybecker.epforuml.camera

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.ybecker.epforuml.CameraActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CameraTest {

    @Test
    fun testCaptureButtonVisible() {
        val scenario = ActivityScenario.launch(CameraActivity::class.java)

        // Check if the capture button is visible
        onView(withId(com.github.ybecker.epforuml.R.id.image_capture_button)).check(matches(isDisplayed()))

        // Check if the preview is visible
        onView(withId(com.github.ybecker.epforuml.R.id.viewFinder)).check(matches(isDisplayed()))

        scenario.close()

    }
}
