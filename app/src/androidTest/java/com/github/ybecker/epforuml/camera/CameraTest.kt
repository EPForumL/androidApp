package com.github.ybecker.epforuml.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.os.RemoteException
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.ybecker.epforuml.CameraActivity
import com.github.ybecker.epforuml.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CameraTest {
    @Before
    fun init() {
        val uiDevice: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val coordinates: Array<Point?> = arrayOfNulls<Point>(4)
        coordinates[0] = Point(248, 1520)
        coordinates[1] = Point(248, 929)
        coordinates[2] = Point(796, 1520)
        coordinates[3] = Point(796, 929)
        try {
            if (!uiDevice.isScreenOn()) {
                uiDevice.wakeUp()
                uiDevice.swipe(coordinates, 10)
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
    @Test
    fun testCaptureButtonVisible() {
        Intents.init()
        val intent = Intent(ApplicationProvider.getApplicationContext(),CameraActivity::class.java)

        val scenario = ActivityScenario.launch<Activity>(intent)

        // Check if the capture button is visible
        onView(withId(R.id.image_capture_button)).check(matches(isDisplayed()))

        // Check if the preview is visible
        onView(withId(R.id.viewFinder)).check(matches(isDisplayed()))

        Intents.release()
        scenario.close()

    }
}
