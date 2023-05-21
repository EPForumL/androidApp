package com.github.ybecker.epforuml

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()

        scenario = ActivityScenario.launch(MainActivity::class.java)
    }


    @Test
    fun guestDoesNotSeeNewQuestionButton() {
        scenario.onActivity {
            MockAuthenticator(it).signOut()
        }

        onView(withId(R.id.new_question_button))
            .check(matches(not(isDisplayed())))
    }


    @Test
    fun loggedInSeesNewQuestionButton() {
        scenario.onActivity {
            MockAuthenticator(it).signIn()
        }

        onView(withId(R.id.new_question_button))
            .check(matches(isDisplayed()))
    }


    @After
    fun end() {
        scenario.close()
    }
}