package com.github.ybecker.epforuml

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants
import android.net.Uri
import androidx.test.espresso.action.ViewActions.scrollTo
import com.github.ybecker.epforuml.authentication.LoginActivity
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test import org.junit.rules.ExpectedException
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class EditPhotoTest {

    @Test
    fun goesBackToNewQuestionWhenDone(){

        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        // go to MainActivity
        onView(withId(R.id.guestButton)).perform(click())
        onView(withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.nav_home)).perform(click())
        onView(withId(R.id.new_question_button)).perform(click())
        onView(withId(R.id.takeImage)).perform(click())
        onView(withId(R.id.image_capture_button)).perform(click())

        onView(withId(R.id.ds_photo_editor_top_button_apply)).check(matches(isDisplayed()))

        onView(withId(R.id.ds_photo_editor_top_button_apply)).perform(click())

        //should now navigate to new question fragment

        onView(withId(R.id.image_uri)).check(matches(isDisplayed()))

    }


    }