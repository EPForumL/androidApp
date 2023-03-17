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
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NightModeTest {
    @Test
    fun testFragmentSettingsDarkMode() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)

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