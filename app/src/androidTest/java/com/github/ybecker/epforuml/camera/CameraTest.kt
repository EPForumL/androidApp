package com.github.ybecker.epforuml.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.ybecker.epforuml.CameraActivity
import com.github.ybecker.epforuml.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CameraActivityTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<CameraActivity> =
        ActivityScenarioRule(CameraActivity::class.java)

    private lateinit var scenario: ActivityScenario<CameraActivity>

    @Before
    fun setUp() {
        scenario = activityRule.scenario
    }

   /* @Test
    fun checkCameraPermission() {
        // Check if camera permission is granted
        val permissionStatus = ActivityCompat.checkSelfPermission(
            scenario.getLifecycle().getCurrentState().getContext(),
            Manifest.permission.CAMERA
        )
        assertThat(permissionStatus,isEqualTo(PackageManager.PERMISSION_GRANTED))
    }*/

    @Test
    fun checkImageCaptureButton() {
        // Check if image capture button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.image_capture_button))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun checkImageCapture() {
        // Test taking a photo
        Espresso.onView(ViewMatchers.withId(R.id.image_capture_button)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.viewFinder)).check(matches(isDisplayed()))

    }

}
