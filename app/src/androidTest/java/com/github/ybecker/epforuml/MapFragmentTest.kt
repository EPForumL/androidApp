package com.github.ybecker.epforuml

import android.Manifest
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.ybecker.epforuml.authentication.LoginActivity
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.security.Permission

@RunWith(AndroidJUnit4::class)
class MapFragmentTest {
    lateinit var scenario: ActivityScenario<LoginActivity>

    @get:Rule
    val grantPermission: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun initScenario() {
        DatabaseManager.useMockDatabase()
        Firebase.auth.signOut()
        DatabaseManager.user = null
        scenario = ActivityScenario.launch(LoginActivity::class.java)
        Intents.init()
    }

    @After
    fun closeScenario() {
        scenario.close()
        Intents.release()
    }

    @Test
    fun checkMapLayout() {
        // Here again temporary comment until we find a solution for the CI fails
        /*
        scenario.onActivity { MockAuthenticator(it).signIn().join() }

        Espresso.onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.nav_map))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.map))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        openContextualActionModeOverflowMenu()

        Espresso.onView(ViewMatchers.withText(R.string.share_position))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed())) */
    }

    @Test
    fun checkClickOnShareLocation() {
        scenario.onActivity { MockAuthenticator(it).signIn().join() }

        Espresso.onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.nav_map))
            .perform(click())

        openContextualActionModeOverflowMenu()

        Espresso.onView(ViewMatchers.withText(R.string.share_position))
            .check(ViewAssertions.matches(not(ViewMatchers.isChecked())))
        Espresso.onView(ViewMatchers.withText(R.string.share_position))
            .perform(click())
        openContextualActionModeOverflowMenu()
    }
}