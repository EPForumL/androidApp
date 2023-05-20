package com.github.ybecker.epforuml.chat

import android.app.Activity
import android.content.Intent
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime


@RunWith(AndroidJUnit4::class)
class RealChatTestWithFirebase {
    private val externUserId = "1"
    private val hostUserId = "0"
    private lateinit var scenario : ActivityScenario<Activity>
    private var oldItemCount : Int = 0

    @Before
    fun setUp(){
        Firebase.auth.signOut()
        DatabaseManager.user = Model.User("0", "Romain", "testEmail1")
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java)
        intent.putExtra("externID", externUserId)


        scenario = ActivityScenario.launch(intent)
        navigateToChat()

    }
    @After
    fun tearDown(){
        scenario.close()
    }

    @Test
    fun addMessageRefresh() {
        val localDateTime = LocalDateTime.now().toString()
        val chat = DatabaseManager.db.addChat(externUserId, hostUserId, localDateTime)
        Thread.sleep(10000)
        Espresso.onView(withText(localDateTime)).check(matches(isDisplayed()))
        /*DatabaseManager.db.removeChat(chat!!.chatId!!)
        Thread.sleep(10000)
        Espresso.onView(withText(localDateTime)).check(doesNotExist())*/
        }
    private fun navigateToChat() {
        Espresso.onView(withContentDescription(R.string.open))
            .perform(click())
        Espresso.onView(withId(R.id.nav_chat)).perform(click())
        scenario.onActivity { activity ->
            val view: RecyclerView = activity.findViewById(R.id.recycler_chat_home)
            view.findViewById<CardView>(R.id.buttonChatWith).performClick()
        }
    }
}
