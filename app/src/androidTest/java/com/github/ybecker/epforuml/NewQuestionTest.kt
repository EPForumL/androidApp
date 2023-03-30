package com.github.ybecker.epforuml

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants
import android.net.Uri
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import com.github.ybecker.epforuml.authentication.LoginActivity
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test import org.junit.rules.ExpectedException
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class NewQuestionTest {
    private lateinit var scenario : ActivityScenario<MainActivity>

    @Before
    fun initTests() {
        Intents.init()
    }

    @After
    fun endTests() {
        Intents.release()
    }

    @Test
    fun goesBackToNewQuestionWhenDone() {

        scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.new_question_button)).perform(click())
        onView(withId(R.id.question_title_edittext)).check(matches(isDisplayed()))
        onView(withId(R.id.question_details_edittext)).check(matches(isDisplayed()))
        onView(withId(R.id.subject_spinner)).check(matches(isDisplayed()))
        onView(withId(R.id.image_uri)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_submit)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_submit)).check(matches(isClickable()))
        onView(withId(R.id.takeImage)).check(matches(isDisplayed()))
        onView(withId(R.id.takeImage)).check(matches(isClickable()))
        onView(withId(R.id.uploadButton)).check(matches(isDisplayed()))
        onView(withId(R.id.uploadButton)).check(matches(isClickable()))


    }

    @Test
    fun setsCorrectlyByIntent() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )

        intent.putExtra("uri", "URI")
        intent.putExtra("fragment", "NewQuestionFragment")
        intent.putExtra("questionTitle", "TITLE")
        intent.putExtra("questionDetails", "DETAILS")

        scenario = ActivityScenario.launch(intent)

        onView(withId(R.id.question_title_edittext)).check(matches(withText("TITLE")))
        onView(withId(R.id.question_details_edittext)).check(matches(withText("DETAILS")))
        onView(withId(R.id.image_uri)).check(matches(withText("URI")))


    }

    @Test
    fun failsWithIncorrectUri(){
        var exception = ExpectedException.none()
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            EditPhotoActivity::class.java
        )

        intent.putExtra("uri", "INVALIDURI")

        ActivityScenario.launch<Activity>(intent)
        exception.expect(IllegalArgumentException::class.java)

    }
}