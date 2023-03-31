package com.github.ybecker.epforuml

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.ybecker.epforuml.authentication.LoginActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

class MyQuestionsTest {


//test that the "You are not connected" message is displayed when the user is not connected
    @Test
    fun notConnectedMessageDisplayed() {
        DatabaseManager.useMockDatabase()
        Firebase.auth.signOut()
        DatabaseManager.user = null
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        // go to MainActivity
        onView(withId(R.id.guestButton)).perform(click())

        onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_my_questions)).perform(click())
        onView(withId(com.github.ybecker.epforuml.R.id.my_questions_layout_parent)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //test if "You are not connected" is displayed
        onView(withId(R.id.not_connected_text_view)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        }

//test that the "No question posted" message is displayed when the user is connected but has no question
    @Test
    fun connectedNoQuestMessageDisplayed() {
        DatabaseManager.useMockDatabase()
        Firebase.auth.signOut()
        val user = DatabaseManager.db.addUser("noQuest", "TestUser").get()
        DatabaseManager.user = user

        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        // go to MainActivity
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.nav_my_questions)).perform(click())

        onView(withId(com.github.ybecker.epforuml.R.id.my_questions_layout_parent)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //test if "No question posted" is displayed
         onView(withId(R.id.no_question)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))


    }

    //test that the question is displayed when the user is connected and has questions
    //user1 is initialised in the mock database
    //user1 has 3 questions

    @Test
    fun connectedQuestMessageDisplayed() {
        DatabaseManager.useMockDatabase()
        Firebase.auth.signOut()
        val user = DatabaseManager.db.addUser("user1", "TestUser").get()
        DatabaseManager.user = user

        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        // go to MainActivity
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.nav_my_questions)).perform(click())

        onView(withId(com.github.ybecker.epforuml.R.id.my_questions_layout_parent)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //test if questions are displayed
        onView(withId(R.id.recycler_forum)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))


    }



    }
