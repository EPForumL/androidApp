package com.github.ybecker.epforuml.chat

import android.content.Intent
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.util.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.features.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ChatHomeTest {
    private lateinit var host : Model.User
    private lateinit var scenario : ActivityScenario<MainActivity>
    private lateinit var intent: Intent

    @Before
    fun setTestsUp(){
        DatabaseManager.useMockDatabase()
        //Firebase.auth.signOut()
        //set up database
        host = DatabaseManager.db.addUser("0", "HostUser", "testEmail").get()
        DatabaseManager.db.addUser("1", "ExternUser1", "testEmail").get()
        DatabaseManager.db.addUser("2", "ExternUser2", "testEmail").get()
        DatabaseManager.db.addUser("3", "ExternUser3", "testEmail").get()

        intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )

        scenario = ActivityScenario.launch(intent)
    }

    @After
    fun tearDown(){
        scenario.close()
    }

    private fun logInIntent() {
        scenario.onActivity {
            MockAuthenticator(it).signIn().join()
            it.startActivity(intent)
        }
    }

    /*
    @Test
    fun chatHomeGetsSetCorrectly(){
        DatabaseManager.user = host
        DatabaseManager.db.addChatsWith("0","1")
        DatabaseManager.db.addChatsWith("0","2")
        DatabaseManager.db.addChatsWith("0","3")

        onView(withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.nav_chat)).perform(click())

        scenario.onActivity { activity ->
            val view : RecyclerView = activity.findViewById(R.id.recycler_chat_home)
            assertEquals(3, view.adapter?.itemCount ?:0 )
        }

    }
     */

    @Test
    fun chatHomeSwitchesFragmentsCorrectly(){
        DatabaseManager.user = host
        DatabaseManager.db.addChatsWith("0","1")
        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_chat)).perform(click())

        scenario.onActivity { activity ->
            val view : RecyclerView = activity.findViewById(R.id.recycler_chat_home)
            view.findViewById<CardView>(R.id.buttonChatWith).performClick()
        }
        onView(withId(R.id.title_chat)).check(matches(withText("ExternUser1")))

    }

    @Test
    fun noMessageWhenSignedOut(){
        DatabaseManager.user = null
        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_chat)).perform(click())
        Thread.sleep(5000)
        onView(withId(R.id.not_connected_text_view)).check(matches(isDisplayed()))
        onView(withId(R.id.newChatWith)).check(matches(not(isDisplayed())))


    }
    @Test
    fun noMessageWhenNoChat(){
        DatabaseManager.user = host
        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_chat)).perform(click())
        onView(withId(R.id.no_chats)).check(matches(isDisplayed()))

    }

    /*
    @Test
    fun chatWithNewUserSwitchesActivity(){
        DatabaseManager.user = host
        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_chat)).perform(click())
        onView(withId(R.id.newChatWith)).perform(click())
        onView(withId(R.id.searchView)).check(matches(isDisplayed()))
        onView(withId(R.id.listView)).check(matches(isDisplayed()))
    }
     */
}
