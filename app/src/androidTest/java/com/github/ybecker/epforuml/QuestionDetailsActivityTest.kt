package com.github.ybecker.epforuml

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionDetailsActivityTest {

    // PASSER UNE QUESTION DANS L'ACTIVITé !!!

    private lateinit var scenario : ActivityScenario<MainActivity>

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    private fun goToFirstElement() {
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(0))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, click()))
    }

    private fun goToThirdElement() {
        onView(withId(R.id.recycler_forum))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(2))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(2, click()))
    }

    @Test
    fun properDisplayOfElementsWhenNoAnswer() {
        goToFirstElement()

        //onView(withId(R.id.qdetails_title)).check(matches(withText("Very long question")))

        // go to question details position
        onView(withId(R.id.answers_recycler))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(0))

        onView(withId(R.id.qdetails_question_content))
            .check(matches(withText(db.getQuestionById("question3").get()?.questionText)))

        // check that there is no answer displayed
        /*
        onView(withId(R.id.answers_recycler))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(1)).check(matches(null))
         */
    }

    @Test
    fun properDisplayOfElementsWhenSeveralAnswers() {
        goToThirdElement()

        //onView(withId(R.id.qdetails_title)).check(matches(withText("About ci")))

        // go to question details position
        onView(withId(R.id.answers_recycler))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(2))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(2,
            check(matches())
            ))

        onView(withId(R.id.qdetails_question_content))
            .check(matches(withText(db.getQuestionById("question1").get()?.questionText)))

        // check that answers are displayed
        onView(withId(R.id.answers_recycler))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(1))
            .check(matches(withId(R.id.qdetails_answer_item)))

        /*
        onView(withId(R.id.qdetails_answer_username))
            .check(matches(withText(db.getAnswerById("answer3").get()?.userId)))

        onView(withId(R.id.qdetails_answer_text))
            .check(matches(withText(db.getAnswerById("answer3").get()?.answerText)))

         */

        // how to check text is correct if it not possible to predict which one is going to be
    // diplayed first
    }


    @After
    fun closing() {
        scenario.close()
    }
}