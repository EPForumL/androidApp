package com.github.ybecker.epforuml

import android.content.Intent
import android.provider.Settings
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import android.content.ContentResolver
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
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
        onView(withId(R.id.recycler_my_questions))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
    }

    @Test
    fun newActivityContainsCorrectData() {
        onView(withId(R.id.recycler_my_questions))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                    click()
                ))

        onView(withId(R.id.qdetails_title))
            .check(ViewAssertions.matches(ViewMatchers.withText("About Scrum master")))
    }


    @Test
    fun isConnectedReturnsTrueWhenDeviceOnline() {
        assertTrue(MainActivity.isConnected())
    }

    // TODO : find a way of toggling airplane mode
/*
    @Test
    fun isConnectedReturnsFalseWhenDeviceOffline() {
        scenario.onActivity {
            Settings.System.putInt(it.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 1)
            it.startActivity(Intent())
        }

        assertFalse(MainActivity.isConnected())
    }

 */

    @Test
    fun cachesProperlySentToQuestionDetailsFromForum() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )

        val emptyAnswers : ArrayList<Model.Answer> = arrayListOf()

        // initialize cache
        intent.putParcelableArrayListExtra("savedQuestions", cache)
        intent.putParcelableArrayListExtra("savedAnswers", emptyAnswers)

        scenario = ActivityScenario.launch(intent)

        Intents.init()

        // go to last QuestionDetailsActivity
        onView(withId(R.id.recycler_my_questions))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    2,
                    click()
                )
            )

        intended(allOf(hasExtra("savedQuestions", cache), hasExtra("savedAnswers", emptyAnswers)))

        Intents.release()
    }


    @After
    fun end() {
        scenario.close()
    }
}