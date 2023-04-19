package com.github.ybecker.epforuml

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.authentication.Authenticator
import com.github.ybecker.epforuml.authentication.FirebaseAuthenticator
import com.github.ybecker.epforuml.authentication.LoginActivity
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionDetailsTest {

    private lateinit var scenario : ActivityScenario<MainActivity>

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()
        Firebase.auth.signInWithEmailAndPassword("jdupont@epfl.ch", "jdpoutn")
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun questionIsClickable() {
        onView(withId(R.id.recycler_forum)).check(matches(isClickable()))
    }

    @Test
    fun newActivityContainsCorrectData() {
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(withId(R.id.qdetails_title)).check(matches(withText("About Scrum master")))
    }

    @Test
    fun backToMainIsCorrect() {
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        onView(withId(R.id.back_to_forum_button)).perform(click())

        onView(withId(R.id.recycler_forum)).check(matches(isDisplayed()))
    }

    @Test
    fun loggedInCanPost() {
        // authentication
        scenario.onActivity { MockAuthenticator(it).signIn() }


        // go to last question
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))

        onView(withId(R.id.write_reply_box)).check(matches(isDisplayed()))
        onView(withId(R.id.post_reply_button)).check(matches(isDisplayed()))
    }

    @Test
    fun cannotPostEmptyAnswer() {
        // authentication
        scenario.onActivity { MockAuthenticator(it).signIn() }


        // go to last question
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))

        // post answer
        onView(withId(R.id.post_reply_button)).perform(click())

        // check displayed
        onView(withId(R.id.answers_recycler))
            .perform(RecyclerViewActions.scrollToLastPosition<ViewHolder>())
            .check(matches(hasDescendant(not(withText("")))))
    }

    @Test
    fun writeAnswerAndPostIsDisplayed() {
        // authentication
        scenario.onActivity { MockAuthenticator(it).signIn() }

        // go to last question
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))

        // post write answer
        onView(withId(R.id.write_reply_box))
            .perform(click())
            .perform(typeText("New answer"))
            .perform(closeSoftKeyboard())

        // post answer
        onView(withId(R.id.post_reply_button)).perform(click())

        // check displayed
        onView(withId(R.id.answers_recycler))
            .perform(RecyclerViewActions.scrollToLastPosition<ViewHolder>())
            .check(matches(hasDescendant(withText("New answer"))))
                // check correct userId
            .check(matches(hasDescendant(withText("0"))))

        // check edittext is now empty (check works)
        onView(withId(R.id.write_reply_box)).check(matches(withText("")))

    }

    @Test
    fun guestUserCannotPostAnswers() {
        scenario.onActivity { MockAuthenticator(it).signOut() }

        // go to second question
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        // check button is not clickable
        onView(withId(R.id.not_loggedin_text)).check(matches(isDisplayed()))
        onView(withId(R.id.not_loggedin_text)).check(matches(withText("Please login to post answers.")))
    }

    @Test
    fun questionEndorseButtonModifyTheCounter() {
        scenario.onActivity { MockAuthenticator(it).signIn() }

        // go to second question
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))
        onView(withText("0"))
        onView(withText("Endorse this"))
        onView(withId(R.id.endorsementButton)).perform(click())
        onView(withText("1"))
        onView(withText("Endorsed"))
        onView(withId(R.id.endorsementButton)).perform(click())
        onView(withText("0"))
        onView(withText("Endorse this"))
    }

    @Test
    fun questionEndorsementStaysWhenQuitting() {
        scenario.onActivity { MockAuthenticator(it).signIn() }

        // go to second question
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        onView(withId(R.id.endorsementButton)).perform(click())
        onView(withId(R.id.back_to_forum_button)).perform(click())
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        onView(withText("Endorsed"))
        onView(withText("1"))
    }

    @Test
    fun answerLikeButtonModifyTheCounter() {
        scenario.onActivity { MockAuthenticator(it).signIn() }

        // go to second question
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))


    }

    @Test
    fun answerLikeStaysWhenQuitting() {}

    @After
    fun closing() {
        scenario.close()
    }
}