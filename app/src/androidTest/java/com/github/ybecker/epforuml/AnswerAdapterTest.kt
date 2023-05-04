package com.github.ybecker.epforuml

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.DatabaseManager.user
import com.github.ybecker.epforuml.database.Model
import org.hamcrest.BaseMatcher
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.Description
import org.junit.runner.RunWith
import java.util.regex.Matcher

/**
 * Tests the display of answers to specific questions in the QuestionDetailsActivity
 */
@RunWith(AndroidJUnit4::class)
class AnswerAdapterTest {

    private lateinit var scenario : ActivityScenario<MainActivity>
    private lateinit var question3 : Model.Question

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()
        db.getUserById("user1").thenAccept{
            user = it!!
        }
        db.addQuestion("user1","course0","Not so long question",
            "TEST FOR CHAT", "")

        val answer6 =
            Model.Answer("answer6", "question4", "answerAdapterTestUser",
                "Nan mais je suis pas d'accord non plus", emptyList(), "")
        db.addAnswer(answer6.userId, answer6.questionId, answer6.answerText)

        db.getQuestionById("question3").thenAccept {
            question3 = it!!
            scenario = ActivityScenario.launch(MainActivity::class.java)
        }
    }

    private fun goToQuestion(questionTitle: String) {
        onView(withText(questionTitle))
            .perform(scrollTo(), click())
    }


    private fun goToFirstElement() {
        // Find the RecyclerView that contains the questions
        goToQuestion(question3.questionTitle)

    }

    private fun goToThirdElement() {
        db.getUserQuestions("user1").thenAccept {
            goToQuestion(it[0].questionTitle)
        }
    }

    @Test
    fun recyclerViewIseDisplayed() {
        goToThirdElement()
        onView(withId(R.id.qdetails_title)).check(matches(isDisplayed()))
        onView(withId(R.id.answers_recycler)).check(matches(isDisplayed()))
    }

    @Test
    fun properDisplayOfElementsWhenNoAnswer() {
        goToFirstElement()

        onView(withId(R.id.qdetails_title)).check(matches(withText(question3.questionTitle)))
    }

    @Test
    fun clickingOnChatLeadsToChat(){
        onView(withId(R.id.recycler_my_questions))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(0))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0,
                click()
            ))
        onView(withId(R.id.chatWithUser)).perform(click())
        onView(withId(R.id.title_chat)).check(matches(isDisplayed()))

    }

    @After
    fun closing() {
        scenario.close()
    }
}