package com.github.ybecker.epforuml

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.authentication.LoginActivity
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class EditPhotoTest {

@Test
    fun navigatesCorrectlyThroughApp() {

        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        onView(ViewMatchers.withId(R.id.guestButton)).perform(click())
        // open navigation drawer
        onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(click())
        onView(ViewMatchers.withId(R.id.nav_home)).perform(click())
        onView(ViewMatchers.withId(R.id.new_question_button)).perform(click())
        onView(ViewMatchers.withId(R.id.takeImage)).perform(ViewActions.scrollTo())
        onView(ViewMatchers.withId(R.id.takeImage)).perform(click())
        onView(ViewMatchers.withId(R.id.image_capture_button)).perform(click())




        //
        //onView(ViewMatchers.withId(R.id.ds_photo_editor_top_button_apply).perform(click()))
    }
}