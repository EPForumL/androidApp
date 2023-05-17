package com.github.ybecker.epforuml

import android.Manifest
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionDetailsWithVoiceNote {

    private lateinit var scenario: ActivityScenario<MainActivity>
    @get:Rule
    var permissionCamera: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO)

    @Before
    fun setUp(){
        Firebase.auth.signOut()
        val user = DatabaseManager.db.addUser("user1", "TestUser", "").get()
        DatabaseManager.user = user

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
        intent.putExtra("fragment", "NewQuestionFragment")
        intent.putExtra("questionTitle", "voicenote")
        intent.putExtra("questionDetails", "voicenote")
        scenario = ActivityScenario.launch(intent)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun submitMakesCorrectUpload() {
            onView(withId(R.id.voice_note_button)).perform(scrollTo(),click())
            Thread.sleep(2000)
            onView(withId(R.id.voice_note_button)).perform(scrollTo(),click())
            onView(withId(R.id.btn_submit)).perform(scrollTo(), click())
            //why does it loop forever here??
            onView(withText("voicenote")).perform(click())
            scenario.onActivity {
                assert(it.findViewById<Button>(R.id.play_note_button).visibility == View.VISIBLE)
            }
    }

    @Test
    fun disableOtherButton(){
        onView(withId(R.id.voice_note_button)).perform(scrollTo(),click())
        scenario.onActivity {
            assertThat(it.findViewById<Button>(R.id.play_note_button), isNotEnabled())
        }
        onView(withId(R.id.voice_note_button)).perform(scrollTo(),click())
    }


}
