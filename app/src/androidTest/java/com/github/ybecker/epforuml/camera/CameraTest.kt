package com.github.ybecker.epforuml.camera

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CameraTest {
/*
    @Test
    fun newQuestionSetsUpWhenIntentFilled() {

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )

        val uri = "thisistheuri"
        intent.putExtra("uri", uri)

        try {
            ActivityScenario.launch<Activity>(intent)

            onView(withId(R.id.new_question_button)).perform(click())

            assertTrue(true)
            onView(withId(R.id.image_uri)).check(matches(withText(uri)))

        } catch (e: Exception) {
            Log.e("NewQuestionFragment", "Error lauching activity: \${e.message}")
        }
    }

 */

    @Test
    fun navigatesCorrectly(){
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {
            MockAuthenticator(it).signOut()
        }

        onView(withId(R.id.new_question_button)).perform(click())
        onView(withId(R.id.takeImage))
            .perform(scrollTo())
            .perform(click())

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        val allowPermissions: UiObject = device.findObject(UiSelector().text("While using the app"))
        if (allowPermissions.exists()) {
            allowPermissions.click()
        }

        onView(withId(R.id.image_capture_button))
            .check(matches(isDisplayed()))
            .perform(click())

        scenario.close()
    }
}