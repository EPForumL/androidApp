package com.github.ybecker.epforuml.chat

import android.app.Activity
import android.content.Intent
import android.widget.LinearLayout
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RealChatTest {
    private lateinit var host : Model.User
    private lateinit var extern : Model.User
    private lateinit var scenario : ActivityScenario<Activity>

    @Before
    fun setTestsUp(){
        DatabaseManager.useMockDatabase()
        Firebase.auth.signOut()
        //set up database
        host = DatabaseManager.db.addUser("0", "HostUser", "testEmail").get()
        extern = DatabaseManager.db.addUser("1", "ExternUser", "testEmail").get()

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java)
        intent.putExtra("externID", extern.userId)

        scenario = ActivityScenario.launch(intent)

    }
    @After
    fun tearDown(){
        scenario.close()
    }

    @Test
    fun chatGetsSetCorrectly(){
        DatabaseManager.user = host
        DatabaseManager.db.addChat(host.userId,extern.userId,"Hey Extern!")
        DatabaseManager.db.addChat(extern.userId,host.userId,"Hey Host!")

        Espresso.onView(withContentDescription(R.string.open))
            .perform(click())
        Espresso.onView(withId(R.id.nav_chat)).perform(ViewActions.click())
        Thread.sleep(5000)

        Espresso.onView(withId(R.id.buttonChatWith)).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.buttonChatWith)).perform(closeSoftKeyboard())
        Espresso.onView(withId(R.id.buttonChatWith)).perform(scrollTo()).perform(click())
        Espresso.onView(withId(R.id.title_chat)).check(matches(withText("ExternUser")))
        Espresso.onView(withId(R.id.send_text)).check(matches(isClickable()))
        Espresso.onView(withId(R.id.edit_text_message)).check(matches(isDisplayed())).check(matches(
            isClickable()
        ))
        scenario.onActivity { activity ->
                val view : RecyclerView = activity.findViewById(R.id.recycler_chat)
                assertEquals(2, view.adapter?.itemCount ?:0 )
            }

    }

    @Test
    fun noMessageWhenSignedOut(){
        DatabaseManager.user = null

        Espresso.onView(withContentDescription(R.string.open))
            .perform(click())
        Espresso.onView(withId(R.id.nav_chat)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.not_connected_text_view)).check(matches(isDisplayed()))


    }

    @Test
    fun noMessageWhenNoChat(){
        DatabaseManager.user = host

        Espresso.onView(withContentDescription(R.string.open))
            .perform(click())
        Espresso.onView(withId(R.id.nav_chat)).perform(click())

        Espresso.onView(withId(R.id.no_chats)).check(matches(isDisplayed()))

    }

    @Test
    fun addMessageWorks(){
        DatabaseManager.user = host
        DatabaseManager.db.addChat(host.userId,extern.userId,"Hey Extern!")
        DatabaseManager.db.addChat(extern.userId,host.userId,"Hey Host!")

        Espresso.onView(withContentDescription(R.string.open))
            .perform(click())
        Espresso.onView(withId(R.id.nav_chat)).perform(click())
        Thread.sleep(10000)

        Espresso.onView(withId(R.id.buttonChatWith)).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.buttonChatWith)).perform(closeSoftKeyboard())
        Espresso.onView(withId(R.id.buttonChatWith)).perform(click())


        Espresso.onView(withId(R.id.send_text)).perform(click())
        scenario.onActivity { activity ->
            val view : RecyclerView = activity.findViewById(R.id.recycler_chat)
            assertEquals(3, view.adapter?.itemCount ?:0 )
        }

    }
}
