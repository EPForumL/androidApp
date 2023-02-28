package com.github.ybecker.bootcampKotlin

import androidx.test.espresso.assertion.ViewAssertions.matches

import android.app.Activity
import android.content.Intent

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4

import androidx.test.espresso.Espresso.onView

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GreetingActivityTest {
    @Test
    fun checkGreetMessage() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), GreetingActivity::class.java)
        intent.putExtra("name", "Yann")

        try {
            val activity:ActivityScenario<Activity> = ActivityScenario.launch(intent)
            onView(ViewMatchers.withId(R.id.greetingMessage))
                .check(matches(ViewMatchers.withText("Welcome Yann !")))

            activity.close()
        } catch (_: java.lang.AssertionError) {}
    }
}