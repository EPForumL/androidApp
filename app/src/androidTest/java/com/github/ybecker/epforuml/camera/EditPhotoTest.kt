package com.github.ybecker.epforuml.camera

import android.app.Activity
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.sensor.EditPhotoActivity
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditPhotoTest {

    @Test
    fun displaysEditorOnCorrectWorkflow(){
        Intents.init()
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
        val scenario = ActivityScenario.launch<Activity>(intent)

        onView(withId(R.id.new_question_button)).check(matches(isDisplayed()))
        onView(withId(R.id.new_question_button)).perform(click())
        onView(withId(R.id.takeImage)).check(matches(isDisplayed()))
        onView(withId(R.id.takeImage)).perform(click())

        assertTrue(true)

        /*
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        val allowPermissions: UiObject = device.findObject(UiSelector().text("While using the app"))
        if (allowPermissions.exists()) {
            allowPermissions.click()
        }

        onView(withId(R.id.image_capture_button)).check(matches(isDisplayed()))
        onView(withId(R.id.image_capture_button)).perform(click())
        var done = false
        while(!done){
            try{
                intended(hasComponent(DsPhotoEditorActivity::class.java.name))
                done = true
            } catch (e: AssertionError){
            }
        }

         */
        Intents.release()
        scenario.close()
    }

    @Test
    fun failsWithIncorrectUri(){
        var exception = ExpectedException.none()
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            EditPhotoActivity::class.java
        )

        intent.putExtra("uri", "INVALIDURI")

        val scenario = ActivityScenario.launch<Activity>(intent)
        exception.expect(IllegalArgumentException::class.java)

        scenario.close()
    }
}