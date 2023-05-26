package com.github.ybecker.epforuml

import android.app.Activity
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.features.camera.EditPhotoActivity
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditPhotoTest {

    @Test
    fun failsWithIncorrectUri(){
        var exception = ExpectedException.none()
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            EditPhotoActivity::class.java
        )

        intent.putExtra("uri", "INVALIDURI")

        val scenario = ActivityScenario.launch<Activity>(intent)
        exception.expect(IllegalArgumentException::class.java)


        Intents.release()
        scenario.close()

    }
}