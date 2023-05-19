package com.github.ybecker.epforuml

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.ybecker.epforuml.authentication.LoginActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Test

class MyQuestionsTest {


//test that the "You are not connected" message is displayed when the user is not connected
    @Test
    fun notConnectedMessageDisplayed() {
        Firebase.auth.signOut()
        DatabaseManager.useMockDatabase()
        DatabaseManager.user = null
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        // go to MainActivity
        onView(withId(R.id.guestButton)).perform(click())

        onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_my_questions)).perform(click())

        //test if "You are not connected" is displayed
        onView(withId(R.id.not_connected_text_view)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
        }

//test that the "No question posted" message is displayed when the user is connected but has no question
    @Test
    fun connectedNoQuestMessageDisplayed() {
        Firebase.auth.signOut()
        DatabaseManager.useMockDatabase()

        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val user = DatabaseManager.db.addUser("noQuest", "NoQuest",  "").get()
        DatabaseManager.user = user
        // go to MainActivity
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.nav_my_questions)).perform(click())

        //test if "No question posted" is displayed
        onView(withId(R.id.no_question)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()

    }

    //test that the question is displayed when the user is connected and has questions
    //user1 is initialised in the mock database
    //user1 has 3 questions

    @Test
    fun connectedQuestMessageDisplayed() {
        Firebase.auth.signOut()
        DatabaseManager.useMockDatabase()
        Firebase.auth.signOut()
        val user = DatabaseManager.db.addUser("user1", "TestUser", "").get()
        DatabaseManager.user = user

        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        // go to MainActivity
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.nav_my_questions)).perform(click())

        //test if questions are displayed
        //onView(withId(R.id.recycler_my_questions)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        scenario.close()
    }


    //test that if we add a question, it is displayed in the my questions page

    @Test
    fun testAddAQuestionInMyQuestion() {

        Firebase.auth.signOut()
        DatabaseManager.useMockDatabase()

        val user = DatabaseManager.db.addUser("user1", "TestUser", "").get()
        DatabaseManager.user = user


        // Launch the fragment
        val scenario = ActivityScenario.launch(LoginActivity::class.java)

        //Scroll to the end of the page
        onView(withId(R.id.home_layout_parent)).perform(ViewActions.swipeUp())

        // Click on the new quest button
        onView(withId(R.id.new_question_button)).perform(click())

        // Check that the new fragment is displayed
        onView(withId(R.id.new_question_scrollview)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //fill the different fields


        //Body of the question
        val questBody = "Text"
        onView(withId(R.id.question_details_edittext)).perform(ViewActions.typeText(questBody))

        //Title of the question
        onView(withId(R.id.question_title_edittext)).perform(ViewActions.typeText("Sample My Question Title"))

        // Close the keyboard
        Espresso.closeSoftKeyboard()

        //Selection of the spinner
        onView(withId(R.id.subject_spinner)).perform(click())
        val secondItem = Espresso.onData(CoreMatchers.anything()).atPosition(1)
        secondItem.perform(click())


        Espresso.onData(
            CoreMatchers.allOf(
                CoreMatchers.`is`(CoreMatchers.instanceOf(String::class.java)),
                CoreMatchers.`is`("Database")
            )
        )


        // Scroll to the end of the page
        onView(withId(R.id.new_question_scrollview)).perform(ViewActions.swipeUp())

        onView(withId(R.id.new_question_scrollview)).perform(ViewActions.swipeUp())

        Thread.sleep(1000)
        // Click on the submit button
        onView(withId(R.id.btn_submit)).perform(click())



        //Check if the question is added to the database
        val dataBaseCourse= DatabaseManager.db.getCourseById("course11")

        val courseQuestions = DatabaseManager.db.getCourseQuestions("Database")
        val addedQuestion = courseQuestions.thenAccept{
            it.filter { quest -> quest.questionText == questBody }
        }

        assertNotNull(addedQuestion)

        //check if the question is displayed in the home page

        //Return home



        onView(ViewMatchers.withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.nav_my_questions)).perform(click())

        Thread.sleep(1000)

        //check if the question is displayed in the my question page
        onView(ViewMatchers.withText("Sample My Question Title")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))



        onView(ViewMatchers.withText("Database")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))





        Thread.sleep(2000)

        Firebase.auth.signOut()


        scenario.close()


    }
}
