package com.github.ybecker.epforuml.chat

import android.app.Activity
import android.content.Intent
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
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
class RealChatTestWithFirebase {
    private lateinit var host : Model.User
    private lateinit var extern : Model.User
    private lateinit var useless : Model.User
    private lateinit var scenario : ActivityScenario<Activity>

    @Before
    fun setTestsUp(){
        Firebase.auth.signOut()
        //set up database
        host = DatabaseManager.db.addUser("0", "HostUser", "testEmail").get()
        extern = DatabaseManager.db.addUser("2", "ExternUser", "testEmail").get()
        useless = DatabaseManager.db.addUser("1", "Useless", "testEmail").get()
        setUpChats()

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
    fun addMessageRefresh() {
        navigateToChat()
        DatabaseManager.db.addChat(extern.userId, host.userId, "GREAT!")
        scenario.onActivity { activity ->
            val view: RecyclerView = activity.findViewById(R.id.recycler_chat)
            assertEquals(4, view.adapter?.itemCount ?: 0)
        }
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

    private fun setUpChats() {
        DatabaseManager.user = host
        DatabaseManager.db.addChatsWith(host.userId, extern.userId)
        DatabaseManager.db.addChatsWith(extern.userId, host.userId)
        DatabaseManager.db.addChat(host.userId, extern.userId, "Hey Extern!")
        DatabaseManager.db.addChat(extern.userId, host.userId, "Hey Host!")
        DatabaseManager.db.addChat(host.userId, extern.userId, "HYD?")
    }
}
