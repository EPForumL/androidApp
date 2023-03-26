package com.github.ybecker.epforuml.camera

import com.github.ybecker.epforuml.R
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.authentication.LoginActivity
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CameraTest {

    @Test
    fun newQuestionSetsUpWhenIntentFilled(){

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
}/*

    @Test
    fun navigatesCorrectly(){
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        // go to MainActivity
        onView(ViewMatchers.withId(R.id.guestButton)).perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.home_layout_parent)).check(matches(ViewMatchers.isDisplayed()))

        // open navigation drawer
        onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.nav_home)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.new_question_button)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.takeImage)).check(matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.takeImage)).perform(ViewActions.scrollTo())
        onView(ViewMatchers.withId(R.id.takeImage)).perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.image_capture_button)).check(matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.image_capture_button)).perform(ViewActions.click())


    }
*/}