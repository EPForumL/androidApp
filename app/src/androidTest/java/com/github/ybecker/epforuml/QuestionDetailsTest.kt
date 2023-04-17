package com.github.ybecker.epforuml

import android.app.ActionBar
import android.content.Intent
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import junit.framework.TestCase.*
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionDetailsTest {

    private lateinit var localScenario : ActivityScenario<QuestionDetailsActivity>

    private val QUESTION_ID = "question1"
    private lateinit var question : Model.Question
    private var cache = arrayListOf<Model.Question>()

    private lateinit var intent : Intent

    @Before
    fun begin() {
        DatabaseManager.useMockDatabase()

        intent = Intent(
            ApplicationProvider.getApplicationContext(),
            QuestionDetailsActivity::class.java
        )

        db.getQuestionById(QUESTION_ID).thenAccept {
            question = it!!

            // add question to intent
            intent.putExtra("question", question)
            intent.putParcelableArrayListExtra("savedQuestions", cache)

            // TODO : remove
            intent.putExtra("test", "")
        }

        localScenario = ActivityScenario.launch(intent)
    }

    private fun logInDetailsActivity() {
        localScenario.onActivity {
            MockAuthenticator(it).signIn()
            it.startActivity(intent)
        }
    }

    private fun logOutDetailsActivity() {
        localScenario.onActivity {
            MockAuthenticator(it).signOut()
            it.startActivity(intent)
        }
    }

    // TODO fix

    /*
    @Test
    fun backToMainIsCorrect() {
        //openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        //onView(withId(android.R.id.home))
        //onView(withContentDescription(R.string.abc_action_bar_up_description))
         //   .perform(click())
        var bar : ActionBar?

        localScenario.onActivity {
            bar = it.actionBar
        }

        onView(withId(R.id.recycler_forum)).check(matches(isDisplayed()))
    }

     */

    @Test
    fun loggedInCanPost() {
        logInDetailsActivity()

        onView(withId(R.id.write_reply_box)).check(matches(isDisplayed()))
        onView(withId(R.id.post_reply_button)).check(matches(isDisplayed()))
    }

    @Test
    fun cannotPostEmptyAnswer() {
        logInDetailsActivity()

        onView(withId(R.id.post_reply_button)).perform(click())

        // check displayed
        onView(withId(R.id.answers_recycler))
            .perform(RecyclerViewActions.scrollToLastPosition<ViewHolder>())
            .check(matches(hasDescendant(not(withText("")))))
    }

    @Test
    fun writeAnswerAndPostIsDisplayed() {
        logInDetailsActivity()

        // post write answer
        onView(withId(R.id.write_reply_box))
            .perform(click())
            .perform(typeText("New answer"))
            .perform(closeSoftKeyboard())

        // post answer
        onView(withId(R.id.post_reply_button)).perform(click())

        // check displayed
        onView(withId(R.id.answers_recycler))
            .perform(RecyclerViewActions.scrollToLastPosition<ViewHolder>())
            .check(matches(hasDescendant(withText("New answer"))))
                // check correct userId
            .check(matches(hasDescendant(withText("0"))))

        // check edittext is now empty (check works)
        onView(withId(R.id.write_reply_box)).check(matches(withText("")))
    }


    @Test
    fun guestUserCannotPostAnswers() {
        logOutDetailsActivity()

        // check button is not clickable
        onView(withId(R.id.not_loggedin_text)).check(matches(isDisplayed()))
        onView(withId(R.id.not_loggedin_text)).check(matches(withText("Please login to post answers.")))
    }

    // TODO fix
/*
    @Test
    fun cacheIsProperlySentToMain() {
        // go back to main
        onView(withId(R.id.back_to_forum_button))
            .perform(click())

        val mainCache : SavedQuestionsCache = intent.getParcelableExtra("savedQuestions") ?: SavedQuestionsCache()

        assertFalse(mainCache.isEmpty())
        assertTrue(mainCache.isQuestionSaved(QUESTION_ID))
    }

 */


    @Test
    fun clickingToggleAltersDrawable() {
        logInDetailsActivity()

        onView(withId(R.id.toggle_save_question))
            .perform(click())

        // TODO : complete test
        //assertTrue(cache.getSavedQuestions().isQuestionSaved(QUESTION_ID))
    }

    // TODO fix
    /*
    @Test
    fun cacheSentToMainComesBackTheSame() {
        logInDetailsActivity()

        onView(withId(R.id.back_to_forum_button))
            .perform(click())

        onView(withId(R.id.recycler_forum))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                    click()
                ))

        val localCache = intent.getParcelableExtra("savedQuestions") ?: SavedQuestionsCache()

        assertFalse(localCache.isEmpty())
        assertTrue(localCache.isQuestionSaved(QUESTION_ID))
    }

     */


    // TODO complete test
    @Test
    fun toggleOnWhenQuestionSaved() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            QuestionDetailsActivity::class.java
        )

        logInDetailsActivity()

        //assertTrue(cache.getSavedQuestions().isQuestionSaved(QUESTION_ID))

        onView(withId(R.id.toggle_save_question))
            //.check(matches(withResourceName(R.drawable.checkmark)))
    }

    // test guest cannot save
    @Test
    fun guestCannotSaveQuestion() {
        logOutDetailsActivity()

        onView(withId(R.id.save_question_layout))
            .check(matches(not(isDisplayed())))
    }


    @Test
    fun loggedInCanSaveQuestion() {
        // authentication
        logInDetailsActivity()

        onView(withId(R.id.save_question_layout))
            .check(matches(isDisplayed()))
    }


    @After
    fun closing() {
        localScenario.close()
    }
}