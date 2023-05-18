package com.github.ybecker.epforuml

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import com.github.ybecker.epforuml.util.onViewWithTimeout.Companion.onViewWithTimeout
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.DatabaseManager.user
import com.github.ybecker.epforuml.database.Model
import com.github.ybecker.epforuml.util.onViewWithTimeout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

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
/*
    @Test
    fun clickingOnChatLeadsToChat(){
        onView(withText(question3.questionTitle))
            .perform(scrollTo())
            .perform(click())
        onView(withId(R.id.chatWithUser)).perform(scrollTo(),click())
        onView(withId(R.id.title_chat)).check(matches(isDisplayed()))

    }

 */
    val testVideoURI = "https://firebasestorage.googleapis.com/v0/b/epforuml-38150.appspot.com/o/PumpedUpKicksDancingKidMeme.mp4?alt=media&token=476c2953-ffe3-4a9d-88b1-62243fd7dd95"
    val testImageURI = "https://firebasestorage.googleapis.com/v0/b/epforuml-38150.appspot.com/o/download.jpg?alt=media&token=2549027a-607c-489f-895b-904ab78ebcd9"

    @Test
    fun ImageVisibleWhenVideoOnHeaderTest(){
        Firebase.auth.signOut()
        val user = db.addUser("user1", "TestUser", "").get()
        DatabaseManager.user = user
        val newQuestionTitle = "TESTUMAGEQUESTION"

        db.availableCourses().thenAccept {
            db.addQuestion("0",it[0].courseId, false, newQuestionTitle, newQuestionTitle, testImageURI)
        }.join()

        onView(withId(R.id.swipe_refresh_layout)).perform(ViewActions.swipeDown())

        onView(withText(newQuestionTitle)).perform(click())

        onViewWithTimeout(withId(R.id.image_question))
        onViewWithTimeout(withId(R.id.video_question), matches(not(isDisplayed())))
    }

    @Test
    fun PlayerViewViewVisibleWhenVideoOnHeaderTest(){
        Firebase.auth.signOut()
        val user = db.addUser("user1", "TestUser", "").get()
        DatabaseManager.user = user
        val newQuestionTitle = "TESTUMAGEQUESTION"

        db.availableCourses().thenAccept {
            db.addQuestion("0",it[0].courseId, false, newQuestionTitle, newQuestionTitle, testVideoURI)
        }.join()

        onView(withId(R.id.swipe_refresh_layout)).perform(ViewActions.swipeDown())

        onView(withText(newQuestionTitle)).perform(click())

        onViewWithTimeout(withId(R.id.video_question))
        onViewWithTimeout(withId(R.id.image_question), matches(not(isDisplayed())))
    }

    @Test
    fun PopUpOnImageTest(){
        //re-use of the test that add a new question with image
        ImageVisibleWhenVideoOnHeaderTest()


        onView(withId(R.id.image_question)).perform(scrollTo())
        onView(withId(R.id.image_question)).perform(click())
        onViewWithTimeout(withId(R.id.popUpLayout))

        onView(withId(R.id.back_button)).perform(click())
        onViewWithTimeout(withId(R.id.popUpLayout), doesNotExist())
    }

    @Test
    fun PopUpOnVideoTest(){
        //re-use of the test that add a new question with video
        PlayerViewViewVisibleWhenVideoOnHeaderTest()

        onView(withId(R.id.video_question)).perform(scrollTo())
        //need to sleep otherwise it doesn't click on the video but on the pause button and it fails
        Thread.sleep(1000)
        onView(withId(R.id.video_question)).perform(click())
        onViewWithTimeout(withId(R.id.popUpLayout))

        onView(withId(R.id.back_button)).perform(click())
        onViewWithTimeout(withId(R.id.popUpLayout), doesNotExist())
    }

    @After
    fun closing() {
        scenario.close()
    }
}