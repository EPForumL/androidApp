package com.github.ybecker.epforuml

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import org.junit.Test
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.ybecker.epforuml.authentication.LoginActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.MockDatabase
import com.github.ybecker.epforuml.database.Model
import junit.framework.TestCase.assertNotNull
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before

class NewQuestionTest {

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()


    }
    @Test
    fun testAddAQuestion() {
        // Launch the fragment
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        // go to MainActivity
        onView(withId(R.id.guestButton)).perform(click())

        onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))

        // Click on the new quest button
        onView(withId(R.id.new_question_button)).perform(click())

        // Check that the new fragment is displayed
        onView(withId(R.id.new_question_scrollview)).check(matches(isDisplayed()))



        //fill the different fields


        //Body of the question
        val questBody = "Text"
        onView(withId(R.id.question_details_edittext)).perform(typeText(questBody))

        //Title of the question
        onView(withId(R.id.question_title_edittext)).perform(typeText("Sample Question Title"))


        //Selection of the spinner
        onView(withId(R.id.subject_spinner)).perform(click())
        val secondItem = onData(anything()).atPosition(1)
        secondItem.perform(click())


        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Database")))


        // Scroll to the end of the page
        onView(withId(R.id.new_question_scrollview)).perform(ViewActions.swipeUp())

        // Click on the submit button
        onView(withId(R.id.btn_submit)).perform(click())

        val dataBaseCourse= db.getCourseById("course11")

        val courseQuestions = db.getCourseQuestions("Database")
        val addedQuestion = courseQuestions.thenAccept{
            it.filter { quest -> quest.questionText == questBody }
        }

        assertNotNull(addedQuestion)


        //Return home
        onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))

    }

    @Test
    fun testGoToAddQuestionFragment() {
        // Launch the fragment
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        // go to MainActivity
        onView(withId(R.id.guestButton)).perform(click())

        onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))

        // Click on the submit button
        onView(withId(R.id.new_question_button)).perform(click())

        // Check that the new fragment is displayed
        onView(withId(R.id.new_question_scrollview)).check(matches(isDisplayed()))

    }

}