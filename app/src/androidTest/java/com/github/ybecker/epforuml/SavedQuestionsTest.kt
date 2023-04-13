package com.github.ybecker.epforuml

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SavedQuestionsTest {

    private lateinit var scenario : ActivityScenario<MainActivity>


    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // go to SavedFragment
        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_saved_questions)).perform(click())
    }

    // not logged in can only see text
    @Test
    fun notLoggedInSeesOnlyText() {
        scenario.onActivity { MockAuthenticator(it).signOut() }

        onView(withId(R.id.recycler_saved_questions)).check(matches(not(isDisplayed())))

        onView(withId(R.id.not_loggedin_text))
            .check(matches(isDisplayed()))
            .check(matches(withText("@string/please_log_in_to_be_able_to_save_questions")))
    }

    // logged sees other text if empty question
    @Test
    fun loggedInNothingSaved() {
        scenario.onActivity { MockAuthenticator(it).signIn() }

        onView(withId(R.id.recycler_saved_questions)).check(matches(not(isDisplayed())))

        onView(withId(R.id.not_loggedin_text))
            .check(matches(isDisplayed()))
            .check(matches(withText("No saved questions.")))
    }

    // logged in can see questions if any

    // logged in can click on question to see details

    @After
    fun finish() {
        scenario.close()
    }
}