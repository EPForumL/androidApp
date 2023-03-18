package com.github.ybecker.epforuml

import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.authentication.LoginActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForumAdapterTest {



    private lateinit var scenario : ActivityScenario<LoginActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(LoginActivity::class.java)
        DatabaseManager.useMockDatabase()

        onView(withId(R.id.guestButton)).perform(click())
    }


    @Test
    fun displayRecyclerTest() {
        onView(withId(R.id.recycler_forum)).check(matches(isDisplayed()))
    }

    @Test
    fun displayQuestionTest() {

        // check first question
        onView(withId(R.id.recycler_forum)).perform(RecyclerViewActions
            .scrollToPosition<RecyclerView.ViewHolder>(0))

        onView(withText(DatabaseManager.db.getQuestionById("question3")?.questionText))
            .check(matches(isDisplayed()))

        // check second question
        onView(withId(R.id.recycler_forum)).perform(RecyclerViewActions
            .scrollToPosition<RecyclerView.ViewHolder>(1))

        onView(withText(DatabaseManager.db.getQuestionById("question2")?.questionText))
            .check(matches(isDisplayed()))

        // check third question
        onView(withId(R.id.recycler_forum)).perform(RecyclerViewActions
            .scrollToPosition<RecyclerView.ViewHolder>(2))

        onView(withText(DatabaseManager.db.getQuestionById("question1")?.questionText))
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

    @After
    fun closeScenario() {
        scenario.close()
    }
}