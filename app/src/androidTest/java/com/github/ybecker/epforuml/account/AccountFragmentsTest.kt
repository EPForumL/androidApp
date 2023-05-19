package com.github.ybecker.epforuml.account

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.firebase.ui.auth.KickoffActivity
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.authentication.LoginActivity
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.util.EspressoIdlingResource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import junit.framework.TestCase.assertTrue

@RunWith(AndroidJUnit4::class)
class AccountFragmentsTest {
    lateinit var scenario: ActivityScenario<LoginActivity>

    /*
    private fun registerIdlingResource() {

        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    private fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
     */

    @Before
    fun initScenario() {
        //registerIdlingResource()

        Firebase.auth.signOut()
        DatabaseManager.user = null
        scenario = ActivityScenario.launch(LoginActivity::class.java)
        Intents.init()
    }

    @After
    fun closeScenario() {
        Intents.release()
        scenario.close()

        //unregisterIdlingResource()
    }

    @Test
    fun checkGuestAccountFragmentLayout() {
        onView(withId(R.id.guestButton))
            .perform(click())
        onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(click())

        onView(withId(R.id.drawer_layout)).perform(ViewActions.swipeUp())

        //InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(withId(R.id.nav_account))
            .perform(click())

        checkGuest()
    }

    @Test
    fun checkSignInOpensSignInIntent() {
        onView(withId(R.id.signInButton)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(KickoffActivity::class.java.name))
    }

    @Test
    fun checkAccountFragmentLayout() {
        DatabaseManager.useMockDatabase()
        scenario.onActivity { MockAuthenticator(it).signIn().join() }

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(click())

        onView(withId(R.id.drawer_layout)).perform(ViewActions.swipeUp())
        onView(withId(R.id.nav_account))
            .perform(click())

        onView(withId(R.id.titleAccount))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.signOutButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.deleteAccountButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun checkSignOutRemovesCurrentUserAndGoesToGuestFragment() {
        DatabaseManager.useMockDatabase()
        scenario.onActivity { MockAuthenticator(it).signIn().join() }
        assertTrue(DatabaseManager.user != null)

        //Thread.sleep(2000)

        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))

        //InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(click())

        onView(withId(R.id.drawer_layout)).perform(ViewActions.swipeUp())
        onView(withId(R.id.nav_account))
            .perform(click())
        onView(withId(R.id.signOutButton))
            .perform(click())

        assertTrue(DatabaseManager.user == null)
        checkGuest()
    }

    @Test
    fun checkDeleteAccountDeletesUserAndGoesToGuestFragment() {
        DatabaseManager.useMockDatabase()
        scenario.onActivity { MockAuthenticator(it).signIn().join() }
        assertTrue(DatabaseManager.user != null)

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))

        onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(click())

        onView(withId(R.id.drawer_layout)).perform(ViewActions.swipeUp())
        onView(withId(R.id.nav_account))
            .perform(click())
        onView(withId(R.id.deleteAccountButton))
            .perform(click())

        assertTrue(DatabaseManager.user == null)
        val user = DatabaseManager.db.getUserById("0").join()
        assertTrue(user == null)
        checkGuest()
    }

    private fun checkGuest() {
        onView(withId(R.id.titleAccountGuest))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.signInButtonAccount))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}