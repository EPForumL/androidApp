package com.github.ybecker.epforuml.account

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.authentication.LoginActivity
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
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

    @Before
    fun initScenario() {
        Firebase.auth.signOut()
        DatabaseManager.user = null
        scenario = ActivityScenario.launch(LoginActivity::class.java)
    }

    @After
    fun closeScenario() {
        scenario.close()
    }

    @Test
    fun checkGuestAccountFragmentLayout() {
        onView(ViewMatchers.withId(R.id.guestButton))
            .perform(click())
        onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(click())
        onView(ViewMatchers.withId(R.id.nav_account))
            .perform(click())

        checkGuest()
    }

    @Test
    fun checkAccountFragmentLayout() {
        scenario.onActivity { MockAuthenticator(it).signIn() }

        onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(click())
        onView(ViewMatchers.withId(R.id.nav_account))
            .perform(click())

        onView(ViewMatchers.withId(R.id.titleAccount))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.signOutButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.deleteAccoutButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun checkSignOutRemovesCurrentUserAndGoesToGuestFragment() {
        scenario.onActivity { MockAuthenticator(it).signIn() }
        assertTrue(DatabaseManager.user != null)

        onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(click())
        onView(ViewMatchers.withId(R.id.nav_account))
            .perform(click())
        onView(ViewMatchers.withId(R.id.signOutButton))
            .perform(click())

        assertTrue(DatabaseManager.user == null)

        checkGuest()
    }

    @Test
    fun checkDeleteAccountDeletesUserAndGoesToGuestFragment() {
        scenario.onActivity { MockAuthenticator(it).signIn() }
        assertTrue(DatabaseManager.user != null)

        onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(click())
        onView(ViewMatchers.withId(R.id.nav_account))
            .perform(click())
        onView(ViewMatchers.withId(R.id.signOutButton))
            .perform(click())

        assertTrue(DatabaseManager.user == null)
        // TODO: delete from database check

        checkGuest()
    }

    private fun checkGuest() {
        onView(ViewMatchers.withId(R.id.titleAccountGuest))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.signInButtonAccount))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}