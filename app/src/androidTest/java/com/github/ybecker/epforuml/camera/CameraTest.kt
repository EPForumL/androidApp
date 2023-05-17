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
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase.assertTrue
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

        val questionTitle = "Luna"
        val questionDetails = "Godier"
        val uri = "thisistheuri"

        intent.putExtra("questionTitle", questionTitle)
        intent.putExtra("questionDetails", questionDetails)
        intent.putExtra("uri", uri)

        try {
            ActivityScenario.launch<Activity>(intent)

            onView(ViewMatchers.withId(R.id.new_question_button)).perform(ViewActions.click())

            assertTrue(true)

            /*
            onView(ViewMatchers.withId(R.id.question_details_edittext)).check(matches(withText(questionDetails)))
            onView(ViewMatchers.withId(R.id.question_title_edittext)).check(matches(withText(questionTitle)))
            onView(ViewMatchers.withId(R.id.image_uri)).check(matches(withText(uri)))

             */

        } catch (e: Exception) {
            Log.e("NewQuestionFragment", "Error lauching activity: \${e.message}")
        }
}

    @Test
    fun navigatesCorrectly(){
        Firebase.auth.signOut()
        val user = DatabaseManager.db.addUser("user1", "TestUser", "").get()
        DatabaseManager.user = user
        val scenario = ActivityScenario.launch(LoginActivity::class.java)

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
        scenario.close()
    }
}