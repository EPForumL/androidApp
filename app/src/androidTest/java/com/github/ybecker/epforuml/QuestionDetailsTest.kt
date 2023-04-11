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
import com.github.ybecker.epforuml.authentication.LoginActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionDetailsTest {

    private lateinit var scenario : ActivityScenario<MainActivity>
    private lateinit var otherScenario : ActivityScenario<LoginActivity>
    private val EXPECTED_COUNT_WHEN_NO_NEW_ANSWER = 5
    private val EXPECTED_COUNT_WHEN_NEW_ANSWER = 6

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()
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

    // if text is empty, no new answer is posted
    /*@Test
    fun guestUserCannotPostAnswers() {
        otherScenario = ActivityScenario.launch(LoginActivity::class.java)

        // guest
        onView(withId(R.id.guestButton)).perform(click())

        // go to second question
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))


        // check button is not clickable
        onView(withId(R.id.post_reply_button)).check(matches(isNotClickable()))

        // check hint is adequate
        onView(withId(R.id.write_reply_box)).check(matches(withText("Please login to post an answer.")))

        otherScenario.close()
    }*/

    @Test
    fun writeAnswerAndPost() {
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))

        onView(withId(R.id.write_reply_box))
            .perform(click())
            .perform(typeText("New answer"))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.post_reply_button)).perform(click())

        onView(withId(R.id.answers_recycler))
            .perform(RecyclerViewActions.scrollToLastPosition<ViewHolder>())
            .check(matches(hasDescendant(withText("New answer"))))
    }


    // edittext is visible, so is the button
    // if the text is not empty, new answer is posted upon button click + edittext ends up empty
    // check if connected user is current


    @After
    fun closing() {
        scenario.close()
    }


    // TODO : see if can make use of that
    fun checkAnswerContent(id: Int, position: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById<View>(id) as View
                v.performClick()
            }
        }
    }
}