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
    private var question3 = db.getQuestionById("question3")




    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()
        db.getUserById("answerAdapterTestUser").thenAccept{
            user = it!!
        }
        db.addQuestion("answerAdapterTestUser","course0","Not so long question",
            "TEST FOR CHAT", "")



        val answer6 =
            Model.Answer("answer6", "question4", "answerAdapterTestUser",
                "Nan mais je suis pas d'accord non plus", emptyList())
        db.addAnswer(answer6.userId, answer6.questionId, answer6.answerText)
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    private fun goToQuestion(questionTitle: String) {
        onView(withText(questionTitle))
            .perform(click())
    }


    private fun goToFirstElement() {
        // Find the RecyclerView that contains the questions
        question3.get()?.let { goToQuestion(it.questionTitle) }

    }

    private fun goToThirdElement() {
        val questionTitle = db.getUserQuestions("answerAdapterTestUser").get()[0].questionTitle
        goToQuestion(questionTitle)
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

        question3.thenAccept {
            onView(withId(R.id.qdetails_title)).check(matches(withText(question3.get()?.questionTitle)))
        }
    }

    //@Test
   // fun clickingOnChatLeadsToChat(){
  //      goToFirstElement()
   //     onView(withId(R.id.chatWithUser)).perform(click())
  //      onView(withId(R.id.title_chat)).check(matches(isDisplayed()))

    //}

    @After
    fun closing() {
        scenario.close()
    }
}