package com.github.ybecker.epforuml.authentication

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before


@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    lateinit var scenario: ActivityScenario<LoginActivity>

    @Before
    fun initTests() {
        Firebase.auth.signOut()
        DatabaseManager.user = null
        Intents.init()
    }

    @After
    fun endTests() {
        Intents.release()
    }

    @Test
    fun checkLoginActivityHasExpectedComponents() {
        scenario = ActivityScenario.launch(LoginActivity::class.java)
        onView(ViewMatchers.withId(R.id.welcomeText))
            .check(matches(ViewMatchers.withText(R.string.login_welcome)))
        onView(ViewMatchers.withId(R.id.signInButton)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.guestButton)).check(matches(isDisplayed()))
    }

    @Test
    fun checkAppSkipsLoginActivityWhenConnected() {
        DatabaseManager.user = Model.User()
        scenario = ActivityScenario.launch(LoginActivity::class.java)
        intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun checkContinueAsGuestGoesToMainActivity() {
        scenario = ActivityScenario.launch(LoginActivity::class.java)

        onView(ViewMatchers.withId(R.id.guestButton))
            .perform(click())

        intended(hasComponent(MainActivity::class.java.name))

        assertTrue(DatabaseManager.user == null)
    }
}