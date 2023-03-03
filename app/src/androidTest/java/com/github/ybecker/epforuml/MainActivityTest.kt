package com.github.ybecker.epforuml

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val testRule = createAndroidComposeRule(MainActivity::class.java)

    private val initialTxt = "Your Name"
    private val buttonTxt = "Click me"

    @Test
    fun checkTextFieldHasCorrectInitialContent() {
        testRule.onNodeWithText(initialTxt).assertExists()
    }

    @Test
    fun checkButtonHasCorrectText() {
        testRule.onNodeWithText(buttonTxt).assertExists()
    }

    @Test
    fun checkTextFieldHasEnteredText() {
        val text = "Yann"
        testRule.onNodeWithText(initialTxt).performTextClearance()
        testRule.onNodeWithText("").performTextInput(text)
        testRule.onNodeWithText(text).assertExists()
    }

    @Test
    fun checkTransitionToGreetingActivity() {
        Intents.init()

        val key = "name"
        val text = "Yann"
        testRule.onNodeWithText(initialTxt).performTextClearance()
        testRule.onNodeWithText("").performTextInput(text)

        testRule.onNodeWithText(buttonTxt).performClick()

        intended(allOf(
            hasExtra(key, text),
            hasComponent(GreetingActivity::class.java.name))
        )

        Intents.release()
    }
}
