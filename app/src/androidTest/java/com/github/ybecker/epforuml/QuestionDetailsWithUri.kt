/*
package com.github.ybecker.epforuml

import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionDetailsWithUri {

    private lateinit var scenario : ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        DatabaseManager.useMockDatabase()
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )

        scenario = ActivityScenario.launch(intent)
    }

    @Test
    fun questionWithStoresImageDisplaysCorrectly(){
        onView(withText("IMAGE TEST")).perform(click())
        scenario.onActivity {
            assert(it.findViewById<ImageView>(R.id.image_question).visibility == View.VISIBLE)
        }
    }

    @After
    fun destroy(){
        scenario.close()

    }


}*/
