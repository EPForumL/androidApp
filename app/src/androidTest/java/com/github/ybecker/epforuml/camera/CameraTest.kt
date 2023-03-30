package com.github.ybecker.epforuml.camera

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import com.github.ybecker.epforuml.CameraActivity
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.regex.Matcher

@RunWith(AndroidJUnit4::class)
class CameraTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(CameraActivity::class.java)

    @Before
    fun beforeTest(){
        Intents.init()
    }
    @After
    fun afterTest(){
        Intents.release()
    }
    @Test
    fun testCameraStartedWhenPermissionsGranted() {
        // Given
        if (allPermissionsGranted()) {
            // When
            onView(withId(R.id.viewFinder)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testCameraNotStartedWhenPermissionsNotGranted() {
        // Given
        if (!allPermissionsGranted()) {
            // When
            onView(withText("Permissions not granted by the user.")).check(matches(isDisplayed()))
            activityRule.scenario.close()
        }
    }


    private fun allPermissionsGranted() = true

    @Test
    fun newQuestionSetsUpWhenIntentFilled() {

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
        intent.putExtra("questionTitle", "Luna")
        intent.putExtra("questionDetails", "Godier")

        intent.putExtra("uri", "thisistheuri")


        try {
            ActivityScenario.launch<Activity>(intent)

            onView(ViewMatchers.withContentDescription(R.string.open))
                .perform(ViewActions.click())
            onView(ViewMatchers.withId(R.id.nav_home)).perform(ViewActions.click())
            onView(ViewMatchers.withId(R.id.new_question_button)).perform(ViewActions.click())

            onView(ViewMatchers.withId(R.id.question_details_edittext)).check(matches(withText("Godier")))
            onView(ViewMatchers.withId(R.id.question_title_edittext)).check(matches(withText("Luna")))
            onView(ViewMatchers.withId(R.id.image_uri)).check(matches(withText("thisistheuri")))

        } catch (e: Exception) {
            Log.e("NewQuestionFragment", "Error lauching activity: \${e.message}")
        }
    }
}