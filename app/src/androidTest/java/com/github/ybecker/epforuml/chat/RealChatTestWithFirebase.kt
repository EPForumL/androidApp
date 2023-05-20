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
import com.github.ybecker.epforuml.database.FirebaseDatabaseAdapter
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@RunWith(AndroidJUnit4::class)
class RealChatTestWithFirebase {
    private lateinit var scenario : ActivityScenario<Activity>
    private lateinit var database: FirebaseDatabase
    private lateinit var db: FirebaseDatabaseAdapter
    private lateinit var romain: Model.User
    private lateinit var theo: Model.User

    @Before
    fun setUp() {

        database = Firebase.database

        // local tests works on the emulator but the CI fails
        // so with the try-catch it work but on the real database...
        try{
            //database.useEmulator("10.0.2.2", 9000)
        }
        catch (r : IllegalStateException){ }

        db = FirebaseDatabaseAdapter(database)

        val firebaseDB = database.reference

        firebaseDB.child("courses").setValue(null)
        firebaseDB.child("users").setValue(null)
        firebaseDB.child("questions").setValue(null)
        firebaseDB.child("answers").setValue(null)
        firebaseDB.child("chats").setValue(null)


        romain = db.addUser("0", "Romain", "testEmail1").get()
        theo = db.addUser("1","Theo", "testEmail2").get()

        db.addChatsWith(romain.userId, theo.userId)
        db.addChat(romain.userId, theo.userId ,"Hi Theo this is Romain!")

        Firebase.auth.signOut()

        DatabaseManager.user = romain

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java)
        intent.putExtra("externID", theo.userId)


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
        val chat = db.addChat(theo.userId, romain.userId, localDateTime)
        Thread.sleep(10000)
        Espresso.onView(withText(localDateTime)).check(matches(isDisplayed()))
        db.removeChat(chat!!.chatId!!)
        Thread.sleep(10000)
        Espresso.onView(withText(localDateTime)).check(doesNotExist())


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
