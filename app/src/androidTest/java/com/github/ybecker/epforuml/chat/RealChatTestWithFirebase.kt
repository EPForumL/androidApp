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
import io.reactivex.Completable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture


@RunWith(AndroidJUnit4::class)
class RealChatTestWithFirebase {
    private lateinit var host : Model.User
    private lateinit var extern : Model.User
    private lateinit var scenario : ActivityScenario<Activity>
    private var oldItemCount : Int = 0

    @Test
    fun addMessageRefresh() {
        Firebase.auth.signOut()
        CompletableFuture.allOf(DatabaseManager.db.getUserById("0"),DatabaseManager.db.getUserById("1")).thenAccept{
            DatabaseManager.db.getUserById("0").thenAccept{
                DatabaseManager.user = it!!
                host = it!!
            }
            DatabaseManager.db.getUserById("1").thenAccept{
                host = it!!
            }

            val intent = Intent(
                ApplicationProvider.getApplicationContext(),
                MainActivity::class.java)
            intent.putExtra("externID", extern.userId)

            scenario = ActivityScenario.launch(intent)
            navigateToChat()
            scenario.onActivity { activity ->
                val view: RecyclerView = activity.findViewById(R.id.recycler_chat)
                oldItemCount= view.adapter?.itemCount ?: 0
            }
            DatabaseManager.db.addChat(extern.userId, host.userId, "GREAT!")
            Thread.sleep(3000)
            scenario.onActivity { activity ->
                val view: RecyclerView = activity.findViewById(R.id.recycler_chat)
                assertEquals(oldItemCount+1, view.adapter?.itemCount ?: 0)
            }
            scenario.close()
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
}
