package com.github.ybecker.epforuml

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
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
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import org.hamcrest.Matchers.allOf
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
    private var cache = arrayListOf<Model.Question>()

    private lateinit var intent : Intent

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()

        scenario = ActivityScenario.launch(MainActivity::class.java)

        intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )

        db.getQuestionById("question1").thenAccept {
            question = it!!
            cache.add(it)
            intent.putParcelableArrayListExtra("savedQuestions", cache)
        }
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
        logInIntent()

        goToSavedFragment()

        onView(withId(R.id.recycler_saved_questions))
            .check(matches(isDisplayed()))

        onView(withText(question.questionTitle))
            .check(matches(isDisplayed()))
    }

    /*@Test
    fun loggedCanClickOnSavedQuestionToSeeDetails() {
        // fill cache
        logInIntent()
        goToSavedFragment()

        onView(withId(R.id.recycler_saved_questions))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, click()))

        intended(allOf(hasExtra("savedQuestions", cache), hasExtra("question", question), hasComponent(QuestionDetailsActivity::class.java.name)))
    }

     */

    // TODO(check if saved questions are still there after restarting the app)

    private fun goToSavedFragment() {
        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_saved_questions)).perform(click())
    }

    private fun logInIntent() {
        scenario.onActivity {
            MockAuthenticator(it).signIn()
            it.startActivity(intent)
        }
    }

    @After
    fun finish() {
        scenario.close()
    }
}