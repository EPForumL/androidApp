package com.github.ybecker.epforuml

import android.app.ActionBar
import android.content.Intent
import android.widget.ImageButton
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
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
import com.github.ybecker.epforuml.util.ImageButtonHasDrawableMatcher
import junit.framework.TestCase.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase
import junit.framework.TestCase.fail
import org.hamcrest.Matcher
import org.hamcrest.Matchers
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
            // add empty list of saved questions
            cache.clear()
            intent.putParcelableArrayListExtra("savedQuestions", cache)

            localScenario = ActivityScenario.launch(intent)
        }
    }

    private fun logInDetailsActivity(intent : Intent) {
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

    @Test
    fun loggedInCanPost() {
        logInDetailsActivity(intent)

        onView(withId(R.id.write_reply_box)).check(matches(isDisplayed()))
        onView(withId(R.id.post_reply_button)).check(matches(isDisplayed()))
    }

    @Test
    fun cannotPostEmptyAnswer() {
        logInDetailsActivity(intent)

        onView(withId(R.id.post_reply_button)).perform(click())

        // check displayed
        onView(withId(R.id.answers_recycler))
            .perform(RecyclerViewActions.scrollToLastPosition<ViewHolder>())
            .check(matches(hasDescendant(not(withText("")))))
    }

    @Test
    fun writeAnswerAndPostIsDisplayed() {
        logInDetailsActivity(intent)

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
        onView(withId(R.id.not_loggedin_text)).check(matches(withText("Please login to post answers and endorsements.")))
    }

    @Test
    fun questionEndorseButtonModifyTheCounter() {
        logInDetailsActivity(intent)

        onView(withText("0"))
        onView(withText("Endorse this"))
        onView(withId(R.id.endorsementButton)).perform(click())
        onView(withText("1"))
        onView(withText("Endorsed"))
        onView(withId(R.id.endorsementButton)).perform(click())
        onView(withText("0"))
        onView(withText("Endorse this"))
    }

    // TODO : fix
    /*
    @Test
    fun questionEndorsementStaysWhenQuitting() {
        logInDetailsActivity()

        onView(withId(R.id.endorsementButton)).perform(click())
        onView(withId(R.id.back_to_forum_button)).perform(click())
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        onView(withText("Endorsed"))
        onView(withText("1"))
    }

     */

    @Test
    fun removeQuestionEndorsementTest(){
        logInDetailsActivity(intent)

        onView(withId(R.id.endorsementButton)).perform(click())
        onView(withText("Endorsed"))
        onView(withText("1"))

        onView(withId(R.id.endorsementButton)).perform(click())
        onView(withText("Endorse this"))
        onView(withText("0"))
    }

    @Test
    fun answerLikeButtonModifyTheCounter() {
        logInDetailsActivity(intent)

        val answerposition = 1

        CounterEquals(answerposition, "0")
        ClickOnLike(answerposition)
        CounterEquals(answerposition, "1")

    }

    // TODO : fix
    /*
    @Test
    fun answerLikeStaysWhenQuitting() {
        logInDetailsActivity()

        val answerposition = 1

        ClickOnLike(answerposition)

        onView(withId(R.id.back_to_forum_button)).perform(click())

        onView(withText("About ci")).perform(click())
        CounterEquals(answerposition, "1")
    }

     */

    @Test
    fun removeAnswerLike() {
        logInDetailsActivity(intent)

        val answerposition = 1

        ClickOnLike(answerposition)

        CounterEquals(answerposition, "1")

        ClickOnLike(answerposition)

        CounterEquals(answerposition, "0")
    }

    @Test
    fun clickingToggleAltersDrawable() {
        logInDetailsActivity(intent)

        onView(withId(R.id.toggle_save_question))
            .check(matches(ImageButtonHasDrawableMatcher.hasDrawable(R.drawable.nav_saved_questions)))

        onView(withId(R.id.toggle_save_question))
            .perform(click())

        onView(withId(R.id.toggle_save_question))
            .check(matches(ImageButtonHasDrawableMatcher.hasDrawable(R.drawable.checkmark)))
    }


    // TODO : fix
    /*
    @Test
    fun toggleOnWhenQuestionSaved() {
        cache.add(question)
        assertNotNull(question)

        val newIntent = Intent(
            ApplicationProvider.getApplicationContext(),
            QuestionDetailsActivity::class.java
        )
        newIntent.putParcelableArrayListExtra("savedQuestions", cache)
        newIntent.putExtra("question", question)

        logInDetailsActivity(newIntent)

        onView(withId(R.id.toggle_save_question))
            .check(matches(ImageButtonHasDrawableMatcher.hasDrawable(R.drawable.checkmark)))
    }

     */


    @Test
    fun guestCannotSaveQuestion() {
        logOutDetailsActivity()

        onView(withId(R.id.save_question_layout))
            .check(matches(not(isDisplayed())))
    }


    @Test
    fun loggedInCanSaveQuestion() {
        // authentication
        logInDetailsActivity(intent)

        onView(withId(R.id.save_question_layout))
            .check(matches(isDisplayed()))
    }


    @After
    fun closing() {
        localScenario.close()
    }

    private fun ClickOnLike(itemPosition:Int){
        onView(withId(R.id.answers_recycler)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(
                itemPosition,
                performOnViewChild(R.id.likeButton, click())
            )
        )
    }


    private fun CounterEquals(itemPosition:Int, value:String){
        onView(withId(R.id.answers_recycler))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(
                    itemPosition,
                    checkCounter(R.id.likeCount, value)
                )
            )
    }

    private fun performOnViewChild(viewId: Int, viewAction: ViewAction): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return click().constraints
            }

            override fun getDescription(): String {
                return "click on a child view with id $viewId"
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.findViewById<View>(viewId)?.let {
                    viewAction.perform(uiController, it)
                }
            }
        }
    }

    private fun checkCounter(viewId: Int, value: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return Matchers.allOf(isAssignableFrom(View::class.java))
            }

            override fun getDescription(): String {
                return "check child view with id $viewId"
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.findViewById<TextView>(viewId)?.let {
                    if(it.text!=value){
                        fail()
                    }
                }
            }
        }
    }
}