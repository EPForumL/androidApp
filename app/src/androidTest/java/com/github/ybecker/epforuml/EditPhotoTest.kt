package com.github.ybecker.epforuml

import android.app.Activity
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import com.github.ybecker.epforuml.authentication.LoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditPhotoTest {
    private lateinit var scenario : ActivityScenario<MainActivity>

    @Before
    fun initTests() {
        Intents.init()
    }

    @After
    fun endTests() {
        Intents.release()
    }

    @Test
    fun displaysEditorOnCorrectWorkflow(){
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
        scenario = ActivityScenario.launch(intent)

        // go to MainActivity
        //onView(withId(R.id.guestButton)).perform(click())
        onView(withId(R.id.new_question_button)).perform(click())

        onView(withId(R.id.takeImage)).perform(click())

        onView(withId(R.id.image_capture_button)).perform(click())
       var done = false
        while(!done){
            try{
                intended(hasComponent(DsPhotoEditorActivity::class.java.name))
                done = true
            } catch (e: AssertionError){
            }
        }
    }



    }