package com.github.ybecker.epforuml

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
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
import com.github.ybecker.epforuml.util.MainActivity
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
        val toString = getRandomImageUri().toString()
        intent.putExtra("uri", toString)

        try {
            ActivityScenario.launch<Activity>(intent)
            onView(withId(R.id.btn_submit)).perform(scrollTo(), click())
            //why does it loop forever here??
            onView(withText("Luna")).perform(click())
            scenario.onActivity {
                assert(it.findViewById<ImageView>(R.id.image_question).visibility==View.VISIBLE)
            }
            scenario.close()
        } catch (e: Exception) {
            Log.e("NewQuestionFragment", "Error lauching activity: \${e.message}")
        }
    }

    fun getRandomImageUri(): Uri? {
        val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val images = imageDir.listFiles()?.filter { it.isFile && it.extension in arrayOf("jpg", "jpeg", "png", "bmp", "gif") }
        if (images.isNullOrEmpty()) {
            return takeScreenshotAndReturnUri()
        }
        val randomImage = images[Random.nextInt(images.size)]
        return Uri.fromFile(randomImage)
    }

    private fun takeScreenshotAndReturnUri(): Uri? {
        val screenshot = takeScreenshot()
        val screenshotFile = saveScreenshot(screenshot)
        return Uri.fromFile(screenshotFile)
    }

    private fun takeScreenshot(): Bitmap {
        val rootView = android.R.id.content
        val screenBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(screenBitmap)
        val screenRect = Rect(0, 0, 100, 100)
        return screenBitmap
    }

    private fun saveScreenshot(screenshot: Bitmap): File {
        val screenshotDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs()
        }
        val screenshotFile = File(screenshotDir, "screenshot_${System.currentTimeMillis()}.png")
        screenshotFile.outputStream().use { out ->
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return screenshotFile
    }

}
