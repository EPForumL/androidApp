/*
package com.github.ybecker.epforuml

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

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
    fun submitMakesCorrectUpload() {

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
        intent.putExtra("questionTitle", "Luna")
        intent.putExtra("questionDetails", "Godier")
        val file = File("com/github/ybecker/epforuml/IMG_1215 (1).JPG")
        assert(file.exists())
        val uri = Uri.fromFile(file)
        intent.putExtra("uri", uri.toString())

        try {
            ActivityScenario.launch<Activity>(intent)

            onView(withContentDescription(R.string.open))
                .perform(click())
            onView(withId(R.id.nav_home)).perform(click())
            onView(withId(R.id.new_question_button)).perform(click())
            onView(withId(R.id.btn_submit)).perform(scrollTo(), click())

            onView(withText("Luna")).perform(click())
            scenario.onActivity {
                assert(it.findViewById<ImageView>(R.id.image_question).visibility==View.VISIBLE)
            }
        } catch (e: Exception) {
            Log.e("NewQuestionFragment", "Error lauching activity: \${e.message}")
        }
    }

    @After
    fun destroy(){
        scenario.close()

    }


}
*/
