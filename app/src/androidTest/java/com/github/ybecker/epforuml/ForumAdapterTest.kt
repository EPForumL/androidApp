package com.github.ybecker.epforuml

import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.concurrent.thread

@RunWith(AndroidJUnit4::class)
class ForumAdapterTest {

    private lateinit var scenario : ActivityScenario<MainActivity>

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }


    @Test
    fun displayRecyclerTest() {
        onView(withId(R.id.recycler_my_questions))
        onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun displayQuestionTest() {

        // check first question
        onView(withId(R.id.recycler_my_questions))

        onView(withText(DatabaseManager.db.getQuestionById("question3").get()?.questionTitle))
            .check(matches(isDisplayed()))

        // check second question
        onView(withId(R.id.recycler_my_questions))

        onView(withText(DatabaseManager.db.getQuestionById("question2").get()?.questionTitle))
            .check(matches(isDisplayed()))

        // check third question
        onView(withId(R.id.recycler_my_questions))

        onView(withText(DatabaseManager.db.getQuestionById("question1").get()?.questionTitle))
            .check(matches(isDisplayed()))
    }

    @Test
    fun stillWorksAfterOtherFragmentTest() {
        onView(withContentDescription(R.string.open))
            .perform(ViewActions.click())
        onView(withId(R.id.nav_settings)).perform(ViewActions.click())

        onView(withContentDescription(R.string.open))
            .perform(ViewActions.click())
        onView(withId(R.id.nav_home)).perform(ViewActions.click())

        displayRecyclerTest()
        displayQuestionTest()
    }


    @Test
    fun scrollToRefreshQuestionsTest() {
        scenario.onActivity { MockAuthenticator(it).signIn().join() }

        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown())

        val testStr = "NEWQUESTIONTEST"
        onView(withText(testStr)).check(doesNotExist())

        DatabaseManager.db.availableCourses().thenAccept {
            DatabaseManager.db.addQuestion("0",it[0].courseId, false, testStr, testStr, "")
        }.join()


        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown())

        onView(withText(testStr)).check(matches(isDisplayed()))

    }

    @After
    fun closeScenario() {
        scenario.close()
    }
}