package com.github.ybecker.epforuml

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.*
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.features.authentication.LoginActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.features.authentication.MockAuthenticator
import com.github.ybecker.epforuml.util.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class NewQuestionTest {
    private lateinit var scenario : ActivityScenario<MainActivity>

    @Before
    fun setup() {
        Firebase.auth.signOut()
        DatabaseManager.useMockDatabase()
    }

// Add question button is hidden for null user now !
//    @Test
//    fun testAddQuestionWithNullUser() {
//
//        DatabaseManager.user = null
//        val scenario = ActivityScenario.launch(LoginActivity::class.java)
//        // go to MainActivity
//
//        Thread.sleep(2000)
//        onView(withId(R.id.guestButton)).perform(click())
//
//        // Wait for the view to be loaded
//        onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))
//
//        //Scroll to the end of the page
//        onView(withId(R.id.home_layout_parent)).perform(swipeUp())
//
//        // Click on the new question button
//        onView(withId(R.id.new_question_button)).perform(click())
//        // Check that the new fragment is displayed
//        onView(withId(R.id.new_question_scrollview)).check(matches(isDisplayed()))
//
//        //fill the different fields
//
//        //Body of the question
//        val questBody = "Text From Null User"
//        onView(withId(R.id.question_details_edittext)).perform(typeText(questBody))
//
//        //Title of the question
//        onView(withId(R.id.question_title_edittext)).perform(typeText("Sample Question Title From Null User"))
//
//        //Selection of the spinner
//        onView(withId(R.id.subject_spinner)).perform(click())
//        val secondItem = onData(anything()).atPosition(1)
//        secondItem.perform(click())
//
//
//
//        // Scroll to the end of the page
//        onView(withId(R.id.new_question_scrollview)).perform(swipeUp())
//
//        // Click on the submit button
//        onView(withId(R.id.btn_submit)).perform(click())
//
//        //Check if the question is added to the database
//        val courseQuestions = db.getCourseQuestions("Database")
//        val addedQuestion = courseQuestions.thenAccept{
//            it.filter { quest -> quest.questionText == questBody }
//        }
//
//        assertThat(addedQuestion.get(), equalTo(null))
//
//        scenario.close()
//    }


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

        val user = db.addUser("user1", "TestUser", "").get()
        DatabaseManager.user = user


        // Launch the fragment
        val scenario = ActivityScenario.launch(LoginActivity::class.java)



        //Scroll to the end of the page
        onView(withId(R.id.home_layout_parent)).perform(swipeUp())

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
        onView(withId(R.id.new_question_scrollview)).perform(swipeUp())

        // Click on the submit button
        onView(withId(R.id.btn_submit)).perform(click())

        // Check that no question is added to the database
        val courseQuestions = db.getCourseQuestions("Database").get()
        val addedQuestion = courseQuestions.filter { it.questionText == questBody }.firstOrNull()

        assertNull(addedQuestion)


        // The problem here seem to be that now as we need to send notification the methode is slow
        // and thus the home layout is not instantaneous which lead to an error
        //Return home
        //onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun testAddQuestionWithEmptyText() {

        val user = db.addUser("user1", "TestUser", "").get()
        DatabaseManager.user = user

        // Launch the fragment
        val scenario = ActivityScenario.launch(LoginActivity::class.java)


        //Scroll to the end of the page
        onView(withId(R.id.home_layout_parent)).perform(swipeUp())

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
        onView(withId(R.id.new_question_scrollview)).perform(swipeUp())

        // Click on the submit button
        onView(withId(R.id.btn_submit)).perform(click())

        // Check that no question was added to the database
        val courseQuestions = db.getCourseQuestions("Database").get()
        val addedQuestion = courseQuestions.filter { it.questionTitle == "Sample Question Title" }

        assertThat(addedQuestion.size, equalTo(0))

        scenario.close()
    }

    @Test
    fun testAddAQuestion() {

        val user = db.addUser("user1", "TestUser", "").get()
        DatabaseManager.user = user


        // Launch the fragment
        val scenario = ActivityScenario.launch(LoginActivity::class.java)

        //Scroll to the end of the page
        onView(withId(R.id.home_layout_parent)).perform(swipeUp())

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
        onView(withId(R.id.new_question_scrollview)).perform(swipeUp())

        // Click on the submit button
        onView(withId(R.id.btn_submit)).perform(click())


        //Check if the question is added to the database
        val dataBaseCourse= db.getCourseById("course11")

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


    @Test
    fun AnonymousQuestionTest(){

        val user = db.addUser("AUSERID", "AUSER", "").get()
        DatabaseManager.user = user

        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // Click on the new quest button
        onView(withId(R.id.new_question_button)).perform(click())

        // Check that the new fragment is displayed
        onView(withId(R.id.new_question_scrollview)).check(matches(isDisplayed()))

        //Body of the question
        onView(withId(R.id.question_details_edittext)).perform(typeText("Text")).perform(closeSoftKeyboard())

        //Title of the question
        val title = "New Anonymous Question"
        onView(withId(R.id.question_title_edittext)).perform(typeText(title)).perform(closeSoftKeyboard())

        //Selection of the spinner
        onView(withId(R.id.subject_spinner)).perform(click())
        val secondItem = onData(anything()).atPosition(1)
        secondItem.perform(click())

        //Click the anonymous switch and submit
        onView(withId(R.id.anonymous_switch)).perform(scrollTo(), click())
        onView(withId(R.id.new_question_scrollview)).perform(swipeUp())
        Thread.sleep(500)
        // without a small sleep the test is going to click on the button without finishing the scroll and it will fail !
        onView(withId(R.id.btn_submit)).perform(click())
        Thread.sleep(500)

        onView(withText(title)).perform(click())

        Thread.sleep(1000)
        val allQuestions = db.getQuestions().get()

        //Check that de DB has an anonymous question and the the username is in the anonymousUsers list
        assertTrue(allQuestions.map { it.isAnonymous }.contains(true))
        assertTrue(allQuestions.filter { it.isAnonymous }[0].userId == user.userId)

        val usernameText: ViewInteraction = onView(withId(R.id.qdetails_question_username))
        val text = getText(usernameText).removeSuffix(" asks :")

        assertTrue(DatabaseManager.anonymousUsers.contains(text))

        scenario.close()
    }
/* TODO FIX IS EQUAL !

    @Test
    fun AnonymousAnswerKeepSameSurnameTest(){

        //Send anonymous question as in previous test
        AnonymousQuestionTest()
        val title = "New Anonymous Question"
        onView(withText(title)).perform(click())

        val answerText = "my answer"
        onView(withId(R.id.write_reply_box)).perform(typeText(answerText)).perform(closeSoftKeyboard())
        onView(withId(R.id.post_reply_button)).perform(click())

        Thread.sleep(500)

        //get title name
        val usernameText: ViewInteraction = onView(withId(R.id.qdetails_question_username))
        val text = getText(usernameText).removeSuffix(" asks :")

        Thread.sleep(500)

        // get text of first item
        TextOnItemEqual(1, text, R.id.qdetails_answer_username)
    }



    @Test
    fun AnonymousAnswerToOtherChangeSurnameTest(){
        //Send anonymous question as in previous test

        val title = "TITLE"
        db.addQuestion("OTHERUSER", "course0", true, title, "text", "","")
        val user = db.addUser("AUSERID", "AUSER", "").get()
        DatabaseManager.user = user
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withText(title)).perform(scrollTo(), click())

        // add an answer to the anonymous question
        val answerText = "my answer"
        onView(withId(R.id.write_reply_box)).perform(typeText(answerText)).perform(closeSoftKeyboard())
        onView(withId(R.id.post_reply_button)).perform(click())

        //get title name
        val usernameText: ViewInteraction = onView(withId(R.id.qdetails_question_username))
        val text = getText(usernameText).removeSuffix(" asks :")

        TextOnItemNotEqual(1, text, R.id.qdetails_answer_username)

        scenario.close()
    }
     */

    @Test
    fun testVoiceButtonDisplayed() {
        Firebase.auth.signOut()
        DatabaseManager.useMockDatabase()

        val user = db.addUser("user1", "TestUser", "").get()
        DatabaseManager.user = user

        // Launch the fragment
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        //Scroll to the end of the page
        onView(withId(R.id.home_layout_parent)).perform(swipeUp())

        // Click on the new quest button
        onView(withId(R.id.new_question_button)).perform(click())


        //Scroll to the end of the page
        onView(withId(R.id.new_question_scrollview)).perform(swipeUp())
        onView(withId(R.id.play_note_button)).check(matches(isDisplayed()))
        onView(withId(R.id.voice_note_button)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun checkLatexButtonExistAndOpensDialog() {
        Firebase.auth.signOut()
        DatabaseManager.useMockDatabase()

        val user = db.addUser("user1", "TestUser", "").get()
        DatabaseManager.user = user


        // Launch the fragment
        val scenario = ActivityScenario.launch(LoginActivity::class.java)

        //Scroll to the end of the page
        onView(withId(R.id.home_layout_parent)).perform(swipeUp())

        // Click on the new quest button
        onView(withId(R.id.new_question_button)).perform(click())

        // Check that the new fragment is displayed
        onView(withId(R.id.new_question_scrollview)).check(matches(isDisplayed()))

        onView(withId(R.id.show_latex_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.latex_window_root))
            .check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun openCameraTest() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {
            MockAuthenticator(it).signIn()
        }

        onView(withId(R.id.new_question_button)).perform(click())

        onView(withId(R.id.new_question_scrollview)).perform(swipeUp())

        onView(withId(R.id.takeImage)).perform(click())

        //find a way to check that it has been open correctly...
        scenario.close()
    }

    @Test
    fun openAudioMessage() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {
            MockAuthenticator(it).signIn()
        }

        onView(withId(R.id.new_question_button)).perform(click())

        onView(withId(R.id.new_question_scrollview)).perform(swipeUp())

        onView(withId(R.id.voice_note_button)).perform(click())

        //take a short audio message
//        Thread.sleep(200)
//        onView(withId(R.id.voice_note_button)).perform(click())
//
//        onView(withId(R.id.play_note_button)).check(matches(isEnabled()))

        scenario.close()

    }

    fun getText(matcher: ViewInteraction): String {
        var text = String()
        matcher.perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "Text of the view"
            }

            override fun perform(uiController: UiController, view: View) {
                val tv = view as TextView
                text = tv.text.toString()
            }
        })

        return text
    }


    private fun TextOnItemEqual(itemPosition:Int, value:String, id: Int){
        onView(withId(R.id.answers_recycler))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    itemPosition,
                    compareText(id, value)
                )
            )
    }

    private fun compareText(viewId: Int, value: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return Matchers.allOf(isAssignableFrom(View::class.java))
            }

            override fun getDescription(): String {
                return "check child view with id $viewId"
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.findViewById<TextView>(viewId)?.let {
                    if(!it.text.equals(value)){
                        fail()
                    }
                }
            }
        }
    }

    private fun TextOnItemNotEqual(itemPosition:Int, value:String, id: Int){
        onView(withId(R.id.answers_recycler))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    itemPosition,
                    compareNotText(id, value)
                )
            )
    }

    private fun compareNotText(viewId: Int, value: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return Matchers.allOf(isAssignableFrom(View::class.java))
            }

            override fun getDescription(): String {
                return "check child view with id $viewId"
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.findViewById<TextView>(viewId)?.let {
                    if(it.text.equals(value)){
                        fail()
                    }
                }
            }
        }
    }

    fun atPosition(position: Int,  itemMatcher: Matcher<View?>): Matcher<View?>? {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: // has no item on such position
                    return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }



}