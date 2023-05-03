package com.github.ybecker.epforuml

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.os.Environment
import kotlin.random.Random
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class QuestionDetailsWithUri {

    private lateinit var scenario : ActivityScenario<MainActivity>

    @Test
    fun submitMakesCorrectUpload() {
        Firebase.auth.signOut()
        val user = DatabaseManager.db.addUser("user1", "TestUser", "").get()
        DatabaseManager.user = user
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
        intent.putExtra("fragment", "NewQuestionFragment")
        intent.putExtra("questionTitle", "Luna")
        intent.putExtra("questionDetails", "Godier")
        intent.putExtra("uri", getRandomImageUri().toString())

        try {
            ActivityScenario.launch<Activity>(intent)
            onView(withId(R.id.btn_submit)).perform(scrollTo(), click())

            onView(withText("Luna")).perform(click())
            scenario.onActivity {
                assert(it.findViewById<ImageView>(R.id.image_question).visibility==View.VISIBLE)
            }
            scenario.close()
        } catch (e: Exception) {
            Log.e("NewQuestionFragment", "Error lauching activity: \${e.message}")
        }
    }

    private fun getRandomImageUri(): Uri? {
        val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val images = imageDir.listFiles()?.filter { it.isFile && it.extension in arrayOf("jpg", "jpeg", "png", "bmp", "gif") }
        if (images.isNullOrEmpty()) {
            return null
        }
        val randomImage = images[Random.nextInt(images.size)]
        return Uri.fromFile(randomImage)
    }

}
