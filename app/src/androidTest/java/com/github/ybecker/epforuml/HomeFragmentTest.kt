package com.github.ybecker.epforuml

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.features.authentication.MockAuthenticator
import com.github.ybecker.epforuml.util.MainActivity
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
        DatabaseManager.user = null
        ActivityScenario.launch(MainActivity::class.java)

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