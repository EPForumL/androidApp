package com.github.ybecker.bootcampKotlin

import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import org.hamcrest.Matchers.allOf

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkTransitionToGreetActivity() {
        Intents.init()

        val textInteraction = onView(ViewMatchers.withId(R.id.mainName))
        textInteraction.perform(
            ViewActions.clearText(),
            ViewActions.typeText("Yann"),
            ViewActions.closeSoftKeyboard()
        )

        val buttonInteraction = onView(ViewMatchers.withId(R.id.mainGoButton))
        buttonInteraction.perform(ViewActions.click())

        intended(allOf(
            hasExtra("name", "Yann"),
            hasComponent(GreetingActivity::class.java.name))
        )

        Intents.release()
    }
}
