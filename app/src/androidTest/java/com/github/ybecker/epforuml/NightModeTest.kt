package com.github.ybecker.epforuml

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.authentication.LoginActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NightModeTest {
    @Test
    fun testFragmentSettingsDarkMode() {
        DatabaseManager.useMockDatabase()
        Firebase.auth.signOut()
        DatabaseManager.user = null
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        // go to MainActivity
        Thread.sleep(2000)
        onView(withId(R.id.guestButton)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))
        // open navigation drawer
        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_settings)).perform(click())
        onView(withId(R.id.switchDark)).perform(click())
        onView(withId(R.id.switchDark)).perform(click())
        scenario.close()
    }

}