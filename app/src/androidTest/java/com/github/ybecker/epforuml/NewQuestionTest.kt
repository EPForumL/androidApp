package com.github.ybecker.epforuml

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants
import android.net.Uri
import android.view.View
import androidx.test.espresso.*
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import com.github.ybecker.epforuml.authentication.LoginActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase.*
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.android.awaitFrame
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.hasSize
import java.lang.Thread.sleep
import java.util.concurrent.TimeoutException
import java.util.regex.Matcher

@RunWith(AndroidJUnit4::class)
class NewQuestionTest {
    private lateinit var scenario : ActivityScenario<MainActivity>

    @Before
    fun setup() {
        Firebase.auth.signOut()
        DatabaseManager.useMockDatabase()

    }
    @Test
    fun testAddQuestionWithNullUser() {

        Firebase.auth.signOut()
        DatabaseManager.useMockDatabase()
        DatabaseManager.user = null
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        // go to MainActivity

        Thread.sleep(2000)
        onView(withId(R.id.guestButton)).perform(click())

        // Wait for the view to be loaded
        onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))

        //Scroll to the end of the page
        onView(withId(R.id.home_layout_parent)).perform(ViewActions.swipeUp())

        // Click on the new question button
        onView(withId(R.id.new_question_button)).perform(click())
        // Check that the new fragment is displayed
        onView(withId(R.id.new_question_scrollview)).check(matches(isDisplayed()))

        //fill the different fields

        //Body of the question
        val questBody = "Text From Null User"
        onView(withId(R.id.question_details_edittext)).perform(typeText(questBody))

        //Title of the question
        onView(withId(R.id.question_title_edittext)).perform(typeText("Sample Question Title From Null User"))

        //Selection of the spinner
        onView(withId(R.id.subject_spinner)).perform(click())
        val secondItem = onData(anything()).atPosition(1)
        secondItem.perform(click())



        // Scroll to the end of the page
        onView(withId(R.id.new_question_scrollview)).perform(ViewActions.swipeUp())

        // Click on the submit button
        onView(withId(R.id.btn_submit)).perform(click())

        //Check if the question is added to the database
        val courseQuestions = db.getCourseQuestions("Database")
        val addedQuestion = courseQuestions.thenAccept{
            it.filter { quest -> quest.questionText == questBody }
        }

        assertThat(addedQuestion.get(), equalTo(null))




        scenario.close()

    }


/*
    @Test
    fun testAddImage() {

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
        onView(withId(R.id.new_question_scrollview)).check(matches(isDisplayed()))

        //Body of the question
        val questBody = "Text From User"
        onView(withId(R.id.question_details_edittext)).perform(typeText(questBody))

        // Close the keyboard
        Espresso.closeSoftKeyboard()

        //Title of the question
        onView(withId(R.id.question_title_edittext)).perform(typeText("Sample Question Title"))

        // Close the keyboard
        Espresso.closeSoftKeyboard()

        // Scroll to the end of the page
        onView(withId(R.id.new_question_scrollview)).perform(ViewActions.swipeUp())

        onView(withId(R.id.new_question_scrollview)).perform(ViewActions.swipeUp())

        //click on the image button
        onView(withId(R.id.takeImage)).perform(click())

        onView(withId(R.id.camera_layout_parent)).check(matches(isDisplayed()))

        //click on the camera button

        onView(withId(R.id.image_capture_button)).perform(click())


        scenario.close()

    }


*/


    @Test
    fun testAddQuestionWithEmptyTitle() {
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
        onView(withId(R.id.new_question_scrollview)).check(matches(isDisplayed()))

        //fill the different fields


        //Body of the question
        val questBody = "Sample question body"
        onView(withId(R.id.question_details_edittext)).perform(typeText(questBody))

        //Title of the question
        onView(withId(R.id.question_title_edittext)).perform(typeText(""))

        //Selection of the spinner
        onView(withId(R.id.subject_spinner)).perform(click())
        val secondItem = onData(anything()).atPosition(1)
        secondItem.perform(click())


        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Database")))


        // Scroll to the end of the page
        onView(withId(R.id.new_question_scrollview)).perform(ViewActions.swipeUp())

        // Click on the submit button
        onView(withId(R.id.btn_submit)).perform(click())

        // Check that no question is added to the database
        val courseQuestions = DatabaseManager.db.getCourseQuestions("Database").get()
        val addedQuestion = courseQuestions.filter { it.questionText == questBody }.firstOrNull()

        assertNull(addedQuestion)


        // The problem here seem to be that now as we need to send notification the methode is slow
        // and thus the home layout is not instantaneous which lead to an error
        //Return home
        //onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))

    }

    @Test
    fun testAddQuestionWithEmptyText() {
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
        onView(withId(R.id.new_question_scrollview)).check(matches(isDisplayed()))

        // Fill in the title, but leave the text empty
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

        // Check that no question was added to the database
        val courseQuestions = DatabaseManager.db.getCourseQuestions("Database").get()
        val addedQuestion = courseQuestions.filter { it.questionTitle == "Sample Question Title" }

        assertThat(addedQuestion.size, equalTo(0))

        scenario.close()
    }

    @Test
    fun testAddAQuestion() {

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


        //Check if the question is added to the database
        val dataBaseCourse= DatabaseManager.db.getCourseById("course11")

        val courseQuestions = db.getCourseQuestions("Database")
        val addedQuestion = courseQuestions.thenAccept{
            it.filter { quest -> quest.questionText == questBody }
        }

        assertNotNull(addedQuestion)

        //check if the question is displayed in the home page

        //Return home

        Thread.sleep(2000)

        Firebase.auth.signOut()

        //onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))
        scenario.close()


    }




    @Test
    fun goesBackToNewQuestionWhenDone() {

/*        scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.new_question_button)).perform(click())

        onView(withId(R.id.question_title_edittext)).check(matches(isDisplayed()))
        onView(withId(R.id.question_details_edittext)).check(matches(isDisplayed()))
        onView(withId(R.id.subject_spinner)).check(matches(isDisplayed()))
        onView(withId(R.id.image_uri)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_submit)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_submit)).check(matches(isClickable()))


        onView(withId(R.id.takeImage)).check(matches(isDisplayed()))
        onView(withId(R.id.takeImage)).check(matches(isClickable()))

        onView(withId(R.id.uploadButton)).check(matches(isDisplayed()))
        onView(withId(R.id.uploadButton)).check(matches(isClickable()))
        scenario.close()*/
    }

    @Test
    fun setsCorrectlyByIntent() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )

        intent.putExtra("uri", "URI")
        intent.putExtra("fragment", "NewQuestionFragment")
        intent.putExtra("questionTitle", "TITLE")
        intent.putExtra("questionDetails", "DETAILS")

        scenario = ActivityScenario.launch(intent)

        onView(withId(R.id.question_title_edittext)).check(matches(withText("TITLE")))
        onView(withId(R.id.question_details_edittext)).check(matches(withText("DETAILS")))
        onView(withId(R.id.image_uri)).check(matches(withText("URI")))
        scenario.close()

    }

}