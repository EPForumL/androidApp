package com.github.ybecker.epforuml

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
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
import com.github.ybecker.epforuml.cache.SavedQuestionsCache
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import junit.framework.TestCase
import junit.framework.TestCase.assertTrue
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    private lateinit var cache : SavedQuestionsCache
    private val QUESTION_ID = "question1"
    private lateinit var question : Model.Question

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()

        cache = SavedQuestionsCache()
        DatabaseManager.db.getQuestionById(QUESTION_ID).thenAccept {
            cache.set(QUESTION_ID, it!!)
            question = it
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
        intent.putExtra("savedQuestions", cache)

        scenario = ActivityScenario.launch(intent)

        // go to last QuestionDetailsActivity
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))

        val newCache : SavedQuestionsCache = intent.getParcelableExtra("savedQuestions") ?: SavedQuestionsCache()

        // what if the bundle from FragmentHome never goes back to MainActivity and tries to directly go to SavedquestionsFragment

        TestCase.assertFalse(newCache.isEmpty())
        assertTrue(newCache.isQuestionSaved(QUESTION_ID))
    }


    @Test
    fun cacheSentToDetailsActivityComesBackTheSame() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )

        // initialize cache
        intent.putExtra("savedQuestions", cache)

        scenario = ActivityScenario.launch(intent)

        Intents.init()

        // go to last QuestionDetailsActivity
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))

        // go back to main
        onView(withId(R.id.back_to_forum_button))
            .perform(click())

        intended(allOf(hasExtra("savedQuestions", cache), hasComponent(MainActivity::class.simpleName)))
        val newCache : SavedQuestionsCache = intent.getParcelableExtra("savedQuestions") ?: SavedQuestionsCache()

        TestCase.assertFalse(newCache.isEmpty())
        assertTrue(newCache.isQuestionSaved(QUESTION_ID))

        Intents.release()
    }

    @Test
    fun cacheSentToHomeFragment() {
        
    }

    // test if properly stored
    // test if properly retrieved
    // test if properly sent

    @After
    fun end() {
        scenario.close()
    }
}