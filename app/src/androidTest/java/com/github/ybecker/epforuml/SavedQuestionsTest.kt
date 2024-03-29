package com.github.ybecker.epforuml

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.features.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import com.github.ybecker.epforuml.basicEntities.questions.QuestionDetailsActivity
import com.github.ybecker.epforuml.util.MainActivity
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SavedQuestionsTest {

    private lateinit var scenario : ActivityScenario<MainActivity>

    private lateinit var question : Model.Question
    private var cache = arrayListOf<Model.Question>()

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()

        scenario = ActivityScenario.launch(MainActivity::class.java)

        db.getQuestionById("question1").thenAccept {
            question = it!!
            cache.add(it)
            MainActivity.saveDataToDevice(arrayListOf(it), arrayListOf(), cache, arrayListOf(), arrayListOf())
        }
    }

    @Test
    fun notLoggedInSeesOnlyText() {
        scenario.onActivity { MockAuthenticator(it).signOut() }

        goToSavedFragment()

        onView(withId(R.id.recycler_saved_questions)).check(matches(not(isDisplayed())))

        onView(withId(R.id.text_login_to_save))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.please_login_to_save_questions)))
    }

    /*
    @Test
    fun loggedInNothingSaved() {
        scenario.onActivity {
            MockAuthenticator(it).signIn()

            // remove cache
            /*
            val answers = it.applicationContext.getSharedPreferences("ANSWERS", MODE_PRIVATE)
            answers.edit().remove("answers").apply()

             */
        }

        goToSavedFragment()

        onView(withId(R.id.recycler_saved_questions)).check(matches(not(isDisplayed())))

        onView(withId(R.id.text_login_to_save))
            .check(matches(isDisplayed()))
            //.check(matches(withText("No saved questions.")))
    }

     */

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

    @Test
    fun loggedCanClickOnSavedQuestionToSeeDetails() {
        // fill cache
        logInIntent()

        Intents.init()

        goToSavedFragment()

        onView(withId(R.id.recycler_saved_questions))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, click()))

        intended(allOf(hasExtra("savedQuestions", cache), hasExtra("question", question), hasComponent(
            QuestionDetailsActivity::class.java.name)))

        Intents.release()
    }

    @Test
    fun cachesProperlySentToQuestionDetailsFromSavedQuestions() {
        logInIntent()

        /*
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )

        val emptyAnswers : ArrayList<Model.Answer> = arrayListOf()

        // initialize cache
        intent.putParcelableArrayListExtra("savedQuestions", cache)
        intent.putParcelableArrayListExtra("savedAnswers", emptyAnswers)

        scenario = ActivityScenario.launch(intent)
         */

        goToSavedFragment()

        Intents.init()

        // go to last QuestionDetailsActivity
        onView(withId(R.id.recycler_saved_questions))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(
                    0,
                    click()
                )
            )

        intended(allOf(hasExtra("savedQuestions", cache), hasExtra("savedAnswers", arrayListOf<Model.Answer>())))

        Intents.release()
    }

    private fun goToSavedFragment() {
        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_saved_questions)).perform(click())
    }

    private fun logInIntent() {
        scenario.onActivity {
            MockAuthenticator(it).signIn()
        }
    }

    @After
    fun finish() {
        scenario.close()
    }
}