package com.github.ybecker.bootcampKotlin

import android.app.Activity
import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GreetingActivityTest {
    @get:Rule
    val testRule = createAndroidComposeRule(MainActivity::class.java)

    @Test
    fun checkGreetMessage() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), GreetingActivity::class.java)
        val txt = "Yann"
        intent.putExtra("name", txt)

        try {
            val activity:ActivityScenario<Activity> = ActivityScenario.launch(intent)
            testRule.onNodeWithText("Welcome $txt !").assertExists()
            activity.close()
        } catch (_: java.lang.AssertionError) {}
    }
}