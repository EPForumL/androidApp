package com.github.ybecker.epforuml.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import com.github.ybecker.epforuml.CameraActivity
import com.github.ybecker.epforuml.R
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.concurrent.thread

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
        Intents.init()
    }

    @After
    fun tearDown(){
        scenario.close()
        Intents.release()

    }


    @Test
    fun checkCameraPermission() {
        // Check if camera permission is granted
        val permissionStatus = ActivityCompat.checkSelfPermission(
            ApplicationProvider.getApplicationContext(),
            Manifest.permission.CAMERA
        )
        assertEquals(permissionStatus,PackageManager.PERMISSION_GRANTED)
    }

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

    @Test
    fun navigatesToEditPhotoActivity() {
        // Test taking a photo
        Espresso.onView(ViewMatchers.withId(R.id.image_capture_button)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.viewFinder)).check(matches(isDisplayed()))

        Thread.sleep(3000)

        intended(hasComponent(DsPhotoEditorActivity::class.java.name))
        }

}
