package com.github.ybecker.epforuml

import android.view.View
import android.widget.Switch
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import junit.framework.TestCase.assertTrue
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.action.ViewActions.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase.fail
import org.hamcrest.Matchers.*
import org.junit.Before


@RunWith(AndroidJUnit4::class)
class CoursesFragmentTest {


    private fun ClickOnSwitch(itemPosition:Int){
        onView(withId(R.id.recyclerViewCourses)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(
            itemPosition,
            performOnViewChild(R.id.subscriptionSwitch, click())
        )
        )
    }


    private fun SwitchIsChecked(itemPosition:Int){
        onView(withId(R.id.recyclerViewCourses))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(itemPosition,
                checkViewViewChild(R.id.subscriptionSwitch)
            ))
    }

    @Before
    fun setUp(){
        DatabaseManager.useMockDatabase()
    }

    @Test
    fun SubscribtionStayWhenSwitchingScreen(){
        DatabaseManager.useMockDatabase()
        Firebase.auth.signOut()
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        val user = db.addUser("0", "TestUser").get()
        DatabaseManager.user = user

        val checkPosition = 0

        onView(withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.nav_courses)).perform(click())
        ClickOnSwitch(checkPosition)
        onView(withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.nav_home)).perform(click())
        onView(withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.nav_courses)).perform(click())
        SwitchIsChecked(checkPosition)

        scenario.close()
    }

    @Test
    fun SubscriptionModifyDatabase(){
        DatabaseManager.useMockDatabase()
        Firebase.auth.signOut()
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        val user = db.addUser("0", "TestUser").get()
        DatabaseManager.user = user

        val checkPosition = 0

        assertTrue(user.subscriptions.isEmpty())

        onView(withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.nav_courses)).perform(click())

        ClickOnSwitch(checkPosition)
        db.getUserSubscriptions(user.userId).thenAccept {
            assertTrue(it.size == 1)
        }

        scenario.close()
    }

    //For later : need to rework database :
    //@Test
    //fun UnsubscriptionModifyDatabase(){}


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

    private fun checkViewViewChild(viewId: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(
                    isAssignableFrom(View::class.java),
                    hasDescendant(withId(viewId))
                )
            }

            override fun getDescription(): String {
                return "check child view with id $viewId"
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.findViewById<Switch>(viewId)?.let {
                    if (!it.isChecked) {
                        fail()
                    }
                }
            }
        }
    }
}