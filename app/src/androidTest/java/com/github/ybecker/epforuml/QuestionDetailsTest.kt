package com.github.ybecker.epforuml

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import com.github.ybecker.epforuml.util.EspressoIdlingResource
import com.github.ybecker.epforuml.util.ImageButtonHasDrawableMatcher
import com.github.ybecker.epforuml.util.onViewWithTimeout.Companion.onViewWithTimeout
import junit.framework.TestCase.fail
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionDetailsTest {

    private lateinit var scenario : ActivityScenario<QuestionDetailsActivity>

    private lateinit var question : Model.Question
    private var cache : ArrayList<Model.Question> = arrayListOf()
    private var answersCache : ArrayList<Model.Answer> = arrayListOf()

    private lateinit var intent : Intent

    private fun ClickOnButton(itemPosition:Int, id:Int){
        onView(withId(R.id.answers_recycler)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(
                itemPosition,
                performOnViewChild(id, click())
            )
        )
    }


    private fun CounterEquals(itemPosition:Int, value:String, id: Int){
        onView(withId(R.id.answers_recycler))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(
                    itemPosition,
                    checkCounter(id, value)
                )
            )
    }

    private fun VisibilityEquals(itemPosition:Int, visibility: Int, id: Int){
        onView(withId(R.id.answers_recycler))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(
                    itemPosition,
                    checkVisibility(id, visibility)
                )
            )
    }

    private fun goToQuestion() {
        onView(withId(R.id.recycler_forum))
            .perform(
                RecyclerViewActions.actionOnItem<ViewHolder>(
                    withText(question.questionTitle),
                    click()
                ))
    }
    private fun logInDetailsActivity() {
        scenario.onActivity {
            MockAuthenticator(it).signIn().join()
            it.startActivity(intent)
        }
    }

    private fun logOutDetailsActivity() {
        scenario.onActivity {
            MockAuthenticator(it).signOut().join()
            it.startActivity(intent)
        }
    }


    private fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    private fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }


    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()

        registerIdlingResource()

        intent = Intent(
            ApplicationProvider.getApplicationContext(),
            QuestionDetailsActivity::class.java
        )

        db.getQuestionById("question1").thenAccept {
            question = it!!

            // add question to intent
            intent.putExtra("question", question)
            // add empty list of saved questions
            cache.clear()
            intent.putParcelableArrayListExtra("savedQuestions", cache)

            answersCache.clear()
            intent.putParcelableArrayListExtra("savedAnswers", answersCache)

            intent.putExtra("comingFrom", "HomeFragment")

            scenario = ActivityScenario.launch(intent)
        }
    }

    @Test
    fun newActivityContainsCorrectData() {
        onView(withId(R.id.qdetails_title)).check(matches(withText(question.questionTitle)))
    }

    @Test
    fun backToMainIsCorrect() {
        onView(withContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description))
            .perform(click())

        onView(withId(R.id.title_forum)).check(matches(isDisplayed()))
    }

/*
    @Test
    fun loggedInCanPost() {
        logInDetailsActivity()

        onView(withId(R.id.write_reply_box)).check(matches(isDisplayed()))
        onView(withId(R.id.post_reply_button)).check(matches(isDisplayed()))
    }

 */

    @Test
    fun cannotPostEmptyAnswer() {
        logInDetailsActivity()

        // post answer
        onView(withId(R.id.post_reply_button)).perform(click())

        // check displayed
        onView(withId(R.id.answers_recycler))
            .perform(RecyclerViewActions.scrollToLastPosition<ViewHolder>())
            .check(matches(hasDescendant(not(withText("")))))
    }

    /*
    @Test
    fun writeAnswerAndPostIsDisplayed() {
        logInDetailsActivity()

        val content = "New answer"

        // post write answer
        onView(withId(R.id.write_reply_box))
            .perform(click())
            .perform(typeText(content))
            .perform(closeSoftKeyboard())

        // post answer
        onView(withId(R.id.post_reply_button)).perform(click())

        // check displayed
        onView(withId(R.id.answers_recycler))
            .perform(RecyclerViewActions.scrollToLastPosition<ViewHolder>())
            .check(matches(hasDescendant(withText(content))))
                // check correct userId
            .check(matches(hasDescendant(withText("0"))))

        // check edittext is now empty (check works)
        onView(withId(R.id.write_reply_box)).check(matches(withText("")))

    }

     */

    @Test
    fun guestUserCannotPostAnswers() {
        logOutDetailsActivity()

        // check button is not clickable
        onView(withId(R.id.not_loggedin_text)).check(matches(isDisplayed()))
        onView(withId(R.id.not_loggedin_text)).check(matches(withText("Please login to post answers and endorsements.")))
    }

    @Test
    fun questionEndorseButtonModifyTheCounter() {
        logInDetailsActivity()

        onView(withId(R.id.notificationCount)).check(matches(withText("0")))
        onView(withId(R.id.addFollowButton)).perform(click())
        onView(withId(R.id.notificationCount)).check(matches(withText("1")))
        onView(withId(R.id.addFollowButton)).perform(click())
        onView(withId(R.id.notificationCount)).check(matches(withText("0")))
    }

    @Test
    fun questionEndorsementStaysWhenQuitting() {
        logInDetailsActivity()

        onView(withId(R.id.addFollowButton)).perform(click())
        onView(withContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description))
            .perform(click())

        onView(withText(question.questionTitle)).perform(click())
        // Scroll to the end of the page
        onView(withId(R.id.question_details_layout)).perform(ViewActions.swipeUp())


        onViewWithTimeout(withId(R.id.notificationCount), matches(withText("1")))
    }

    @Test
    fun removeLikeTest() {
        logInDetailsActivity()

        val answerposition = 1

        CounterEquals(answerposition, "0", R.id.likeCount)
        ClickOnButton(answerposition, R.id.likeButton)
        CounterEquals(answerposition, "1", R.id.likeCount)
        ClickOnButton(answerposition, R.id.likeButton)
        CounterEquals(answerposition, "0", R.id.likeCount)

    }

    @Test
    fun answerLikeButtonModifyTheCounter() {
        logInDetailsActivity()

        val answerposition = 1

        CounterEquals(answerposition, "0", R.id.likeCount)
        ClickOnButton(answerposition, R.id.likeButton)
        CounterEquals(answerposition, "1", R.id.likeCount)

    }

    /*
    @Test
    fun answerLikeStaysWhenQuitting() {
        logInDetailsActivity()

        val answerposition = 1

        ClickOnButton(answerposition, R.id.likeButton)

        onView(withContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description))
            .perform(click())

        onView(withText(question.questionTitle)).perform(click())
        CounterEquals(answerposition, "1", R.id.likeCount)
    }
     */

    fun endorseAnswerButtonTest(){
        logInDetailsActivity()

        db.addStatus(DatabaseManager.user?.userId ?: "", "course1", UserStatus.ASSISTANT)

        val itemPosition = 1

        VisibilityEquals(itemPosition, View.GONE, R.id.endorsementText)

        ClickOnButton(itemPosition, R.id.endorsementButton)

        VisibilityEquals(itemPosition, View.VISIBLE, R.id.endorsementText)
    }


    @Test
    fun removeAnswerEndorsementTest(){
        logInDetailsActivity()

        db.addStatus(DatabaseManager.user?.userId ?: "", "course1", UserStatus.ASSISTANT)

        val itemPosition = 1

        VisibilityEquals(itemPosition, View.GONE, R.id.endorsementText)

        ClickOnButton(itemPosition, R.id.endorsementButton)

        VisibilityEquals(itemPosition, View.VISIBLE, R.id.endorsementText)

        ClickOnButton(itemPosition, R.id.endorsementButton)

        VisibilityEquals(itemPosition, View.GONE, R.id.endorsementText)
    }

    @Test
    fun endorseButtonIsVisibleOnlyForStatusUsersTest(){
        logInDetailsActivity()

        val itemPosition = 1

        VisibilityEquals(itemPosition, View.GONE, R.id.endorsementButton)

        DatabaseManager.db.addStatus(DatabaseManager.user?.userId ?: "test_user", "course1", UserStatus.TEACHER)

        onView(withContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description))
            .perform(click())

        onView(withText("About ci")).perform(click())

        VisibilityEquals(itemPosition, View.VISIBLE, R.id.endorsementButton)
    }

    @Test
    fun answerEndorsementStaysWhenQuitting() {
        logInDetailsActivity()

        db.addStatus(DatabaseManager.user?.userId ?: "", "course1", UserStatus.ASSISTANT)

        val itemPosition = 1

        VisibilityEquals(itemPosition, View.GONE, R.id.endorsementText)

        ClickOnButton(itemPosition, R.id.endorsementButton)

        VisibilityEquals(itemPosition, View.VISIBLE, R.id.endorsementText)

        onView(withContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description))
            .perform(click())

        onView(withText(question.questionTitle)).perform(click())

        VisibilityEquals(itemPosition, View.VISIBLE, R.id.endorsementText)
    }

    /*
    @Test
    fun clickingToggleAltersDrawable() {
        logInDetailsActivity()
        Thread.sleep(1000)
        onView(withId(R.id.toggle_save_question))
            .check(matches(ImageButtonHasDrawableMatcher.hasDrawable(R.drawable.nav_saved_questions)))
        Thread.sleep(1000)
        onView(withId(R.id.toggle_save_question))
            .perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.toggle_save_question))
            .check(matches(ImageButtonHasDrawableMatcher.hasDrawable(R.drawable.checkmark)))
    }

     */

    @Test
    fun toggleOnWhenQuestionSaved() {
        cache.add(question)
        intent.putParcelableArrayListExtra("savedQuestions", cache)

        logInDetailsActivity()

        onView(withId(R.id.toggle_save_question))
            .check(matches(ImageButtonHasDrawableMatcher.hasDrawable(R.drawable.checkmark)))
    }

    /*
    @Test
    fun guestCannotSaveQuestion() {
        logOutDetailsActivity()

        onView(withId(R.id.toggle_save_question))
            .check(matches(not(isDisplayed())))
    }

     */


    @Test
    fun loggedInCanSaveQuestion() {
        // authentication
        logInDetailsActivity()

        onView(withId(R.id.toggle_save_question))
            .check(matches(isDisplayed()))
    }

    @Test
    fun scrollToRefreshAnswers() {
        val testQuStr = "NEWQUESTIONTEST"
        var questionId: String? = null
        db.availableCourses().thenAccept {
            db.addQuestion("0",it[0].courseId, false,testQuStr, testQuStr, "null","null").thenAccept{

                questionId = it.questionId
            }
        }.join()

        db.getQuestionById(questionId!!).thenAccept { newQuestion ->
            intent.putExtra("question", newQuestion)

            logInDetailsActivity()

            val testAnsStr =  "NEWANSWER"
            onView(withText(testAnsStr)).check(ViewAssertions.doesNotExist())
            db.addAnswer("0",questionId?:"", testAnsStr)

            onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown())
            onView(withText(testAnsStr)).check(matches(isDisplayed()))
        }
    }


    @Test
    fun goesBackToForumWhenComingFromForum() {
        onView(withContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description))
            .perform(click())

        onView((withId(R.id.title_forum))).check(matches(isDisplayed()))
    }

    @Test
    fun goesBackToSavedQWhenComingSavedQ() {
        intent.putExtra("comingFrom", "SavedQuestionsFragment")
        scenario.onActivity {
            it.startActivity(intent)
        }

        onView(withContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description))
            .perform(click())

        onView((withId(R.id.title_saved))).check(matches(isDisplayed()))

    }


    // check if no connection still displays content of question

    @After
    fun closing() {
        scenario.close()

        unregisterIdlingResource()
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

    private fun checkVisibility(viewId: Int, visibility: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "check if view is visible or not"
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(View::class.java)
            }

            override fun perform(uiController: UiController?, view: View?) {
                if (view == null) {
                    fail("View is null")
                }
                view?.findViewById<View>(viewId).let { textView ->
                    if (textView == null) {
                        fail("TextView is null")
                    }

                    // check visibility
                    val visibilityMatcher = when (visibility) {
                        View.VISIBLE -> Matchers.`is`(View.VISIBLE)
                        View.INVISIBLE -> Matchers.`is`(View.INVISIBLE)
                        View.GONE -> Matchers.`is`(View.GONE)
                        else -> throw IllegalArgumentException("Invalid visibility argument")
                    }

                    assertThat(textView?.visibility, visibilityMatcher)
                }
            }
        }
    }
}