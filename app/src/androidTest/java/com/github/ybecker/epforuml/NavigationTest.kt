package com.github.ybecker.epforuml

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.features.authentication.LoginActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @Test
    fun testFragmentNavigation() {
        Firebase.auth.signOut()
        DatabaseManager.user = null
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        // go to MainActivity
        onView(withId(R.id.guestButton)).perform(click())

        onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))

        // open navigation drawer
        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_settings)).perform(click())
        onView(withId(R.id.settings_layout_parent)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_account)).perform(ViewActions.scrollTo())
        onView(withId(R.id.nav_account)).perform(click())

        onView(withId(R.id.account_layout_parent)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_my_questions)).perform(click())
        onView(withId(R.id.fragment_my_questions_layout)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_saved_questions)).perform(click())
        onView(withId(R.id.saved_questions_layout_parent)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_courses)).perform(click())
        onView(withId(R.id.courses_layout_parent)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_home)).perform(click())
        onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_chat)).perform(click())
        onView(withId(R.id.chat_home__layout)).check(matches(isDisplayed()))

        scenario.close()

    }
}


