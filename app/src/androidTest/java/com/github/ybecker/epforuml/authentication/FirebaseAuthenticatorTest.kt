package com.github.ybecker.epforuml.authentication

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.github.ybecker.epforuml.R
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before


@RunWith(AndroidJUnit4::class)
class FirebaseAuthenticatorTest {
    @get:Rule
    val testRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun checkSignInButtonOpensSignInActivity() {
        onView(ViewMatchers.withId(R.id.signInButton)).perform(click())
        onView(ViewMatchers.withText("Sign in with Google")).check(matches(isDisplayed()))
        onView(ViewMatchers.withText("Sign in with email")).check(matches(isDisplayed()))
    }
}