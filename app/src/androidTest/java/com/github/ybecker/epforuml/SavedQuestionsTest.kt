package com.github.ybecker.epforuml

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SavedQuestionsTest {

    private lateinit var scenario : ActivityScenario<MainActivity>

    private lateinit var question : Model.Question
    private lateinit var cache : ArrayList<Model.Question>

    private lateinit var intent : Intent

    @BeforeClass
    fun getDB() {
        DatabaseManager.useMockDatabase()

        db.getQuestionById("question1").thenAccept {
            question = it!!
            cache.add(it)
        }
    }


    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        intent = Intent(
                ApplicationProvider.getApplicationContext(),
                QuestionDetailsActivity::class.java
        )
    }

    // TODO : fix --> add cache
    // not logged in can only see text
    @Test
    fun notLoggedInSeesOnlyText() {
        scenario.onActivity { MockAuthenticator(it).signOut() }

        goToSavedFragment()

        onView(withId(R.id.recycler_saved_questions)).check(matches(not(isDisplayed())))

        onView(withId(R.id.text_login_to_save))
            .check(matches(isDisplayed()))
            .check(matches(withText("Please log in to be able to save questions.")))
    }

    // logged sees other text if empty question
    @Test
    fun loggedInNothingSaved() {
        scenario.onActivity { MockAuthenticator(it).signIn() }

        goToSavedFragment()

        onView(withId(R.id.recycler_saved_questions)).check(matches(not(isDisplayed())))

        onView(withId(R.id.text_login_to_save))
            .check(matches(isDisplayed()))
            .check(matches(withText("No saved questions.")))
    }

    @Test
    fun loggedCanSeeSavedQuestion() {
        // fill cache
        scenario = ActivityScenario.launch(intent)
        scenario.onActivity { MockAuthenticator(it).signIn() }

        goToSavedFragment()

        onView(withId(R.id.recycler_saved_questions))
            .perform(RecyclerViewActions.scrollToLastPosition<RecyclerView.ViewHolder>())

        onView(withText(question.questionTitle))
            .check(matches(isDisplayed()))
    }

    @Test
    fun loggedCanClickOnSavedQuestionToSeeDetails() {
        // fill cache
        scenario = ActivityScenario.launch(intent)
        scenario.onActivity { MockAuthenticator(it).signIn() }

        Intents.init()

        goToSavedFragment()

        onView(withId(R.id.recycler_saved_questions))
            .perform(RecyclerViewActions.scrollToLastPosition<RecyclerView.ViewHolder>())
            .perform(click())

        intended(hasComponent(QuestionDetailsActivity::class.java.name))

        Intents.release()
    }

    // TODO(check cache is properly transmitted and retrieved)

    // TODO(check if saved questions are still there after restarting the app)

    fun goToSavedFragment() {
        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_saved_questions)).perform(click())
    }

    @After
    fun finish() {
        scenario.close()
    }
}