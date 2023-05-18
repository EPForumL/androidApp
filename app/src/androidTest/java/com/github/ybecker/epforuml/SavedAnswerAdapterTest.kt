package com.github.ybecker.epforuml

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SavedAnswerAdapterTest {
    private lateinit var scenario : ActivityScenario<MainActivity>

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }


    @Test
    fun displayRecyclerTest() {
        onView(withId(R.id.recycler_my_questions))
        onView(withId(R.id.home_layout_parent))
            .check(matches(isDisplayed()))
    }

    @Test
    fun displayQuestionTest() {

        // check first question
        Espresso.onView(ViewMatchers.withId(R.id.recycler_my_questions))

        Espresso.onView(
            ViewMatchers.withText(
                DatabaseManager.db.getQuestionById("question3").get()?.questionTitle
            )
        )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // check second question
        Espresso.onView(ViewMatchers.withId(R.id.recycler_my_questions))

        Espresso.onView(
            ViewMatchers.withText(
                DatabaseManager.db.getQuestionById("question2").get()?.questionTitle
            )
        )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // check third question
        Espresso.onView(ViewMatchers.withId(R.id.recycler_my_questions))

        Espresso.onView(
            ViewMatchers.withText(
                DatabaseManager.db.getQuestionById("question1").get()?.questionTitle
            )
        )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun stillWorksAfterOtherFragmentTest() {
        Espresso.onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.nav_settings)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.nav_home)).perform(ViewActions.click())

        displayRecyclerTest()
        displayQuestionTest()
    }


    @Test
    fun scrollToRefreshQuestionsTest() {
        scenario.onActivity { MockAuthenticator(it).signIn().join() }

        Espresso.onView(ViewMatchers.withId(R.id.swipe_refresh_layout))
            .perform(ViewActions.swipeDown())

        val testStr = "NEWQUESTIONTEST"
        Espresso.onView(ViewMatchers.withText(testStr)).check(ViewAssertions.doesNotExist())

        DatabaseManager.db.availableCourses().thenAccept {
            DatabaseManager.db.addQuestion("0",it[0].courseId, false, testStr, testStr, "", "")
        }.join()


        Espresso.onView(ViewMatchers.withId(R.id.swipe_refresh_layout))
            .perform(ViewActions.swipeDown())

        Espresso.onView(ViewMatchers.withText(testStr))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @After
    fun closeScenario() {
        scenario.close()
    }
}