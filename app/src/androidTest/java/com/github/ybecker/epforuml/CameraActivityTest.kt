package com.github.ybecker.epforuml

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.authentication.LoginActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CameraActivityTest {

    private lateinit var scenario : ActivityScenario<CameraActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(CameraActivity::class.java)
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        if (!hasPermissions(*permissions)) {
            scenario.onActivity {
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
        onView(withId(R.id.image_capture_button)).check(matches(isDisplayed()))
        Thread.sleep(10000) // Wait for photo to be taken
        //onView(withText("filter")).check(matches(isDisplayed()))
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
