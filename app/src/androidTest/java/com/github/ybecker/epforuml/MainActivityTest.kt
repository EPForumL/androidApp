package com.github.ybecker.epforuml

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    private val QUESTION_ID = "question1"
    private lateinit var question : Model.Question

    private var cache = ArrayList<Model.Question>()

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()

        DatabaseManager.db.getQuestionById(QUESTION_ID).thenAccept {
            question = it!!
            cache.add(it)
        }

        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun questionIsClickable() {
        onView(withId(R.id.recycler_forum))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
    }


    @Test
    fun newActivityContainsCorrectData() {
        onView(withId(R.id.recycler_forum))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                    click()
                ))

        onView(withId(R.id.qdetails_title))
            .check(ViewAssertions.matches(ViewMatchers.withText("About Scrum master")))
    }

    @Test
    fun cacheIsProperlySentToDetailsActivity() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )

        // initialize cache
        intent.putParcelableArrayListExtra("savedQuestions", cache)

        scenario = ActivityScenario.launch(intent)

        Intents.init()

        // go to last QuestionDetailsActivity
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))

        intended(hasExtra("savedQuestions", cache))

        Intents.release()
    }

    // TODO test if can also access cache info ?

    @After
    fun end() {
        scenario.close()
    }
}