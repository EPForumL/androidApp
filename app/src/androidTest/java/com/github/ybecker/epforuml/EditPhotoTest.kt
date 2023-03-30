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

    lateinit var scenario: ActivityScenario<LoginActivity>

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

        scenario = ActivityScenario.launch(LoginActivity::class.java)

        // go to MainActivity
        onView(withId(R.id.guestButton)).perform(click())
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

        //onView(withId(R.id.ds_photo_editor_top_button_apply)).perform(click())

        //should now navigate to new question fragment

        //onView(withId(R.id.image_uri)).check(matches(isDisplayed()))

    }
    @Test
    fun failsWithIncorrectUri(){
        var exception = ExpectedException.none()
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            EditPhotoActivity::class.java
        )

        intent.putExtra("uri", "INVALIDURI")

        ActivityScenario.launch<Activity>(intent)
        exception.expect(IllegalArgumentException::class.java)

    }


    }