package com.github.ybecker.epforuml.camera

import android.Manifest
import com.github.ybecker.epforuml.R
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.CameraActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CameraActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(CameraActivity::class.java)

    @Before
    fun setUp() {
        // Grant necessary permissions
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        if (!hasPermissions(*permissions)) {
            activityRule.scenario.onActivity {
                ActivityCompat.requestPermissions(
                    it,
                    permissions,
                    0
                )
            }
            Thread.sleep(2000) // Wait for permissions dialog to show up
            onView(withText(android.R.string.ok)).perform(click())
        }
    }

    @Test
    fun testTakePhoto() {
        onView(withId(R.id.image_capture_button)).perform(click())
        Thread.sleep(2000) // Wait for photo to be taken
        onView(withId(R.id.ds_photo_editor_top_button_apply)).check(matches(isDisplayed()))
    }

    private fun hasPermissions(vararg permissions: String): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(
                ApplicationProvider.getApplicationContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
