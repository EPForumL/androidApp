package com.github.ybecker.epforuml

import android.app.Activity
import android.view.Gravity
import androidx.appcompat.widget.Toolbar
import androidx.compose.material3.DrawerValue
import androidx.drawerlayout.widget.DrawerLayout
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @Test
    fun testFragmentNavigation() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))

        // open navigation drawer
        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_settings)).perform(click())
        onView(withId(R.id.settings_layout_parent)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_account)).perform(click())
        onView(withId(R.id.account_layout_parent)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_my_questions)).perform(click())
        onView(withId(R.id.my_questions_layout_parent)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_saved_questions)).perform(click())
        onView(withId(R.id.saved_questions_layout_parent)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.open))
            .perform(click())
        onView(withId(R.id.nav_home)).perform(click())
        onView(withId(R.id.home_layout_parent)).check(matches(isDisplayed()))

        scenario.close()
    }
}

/*fun <T: Activity> ActivityScenario<T>.getToolbarNavigationContentDescriptor(): String {
    var description = ""
    onActivity {
        description = it.findViewById<Toolbar>(R.id.toolbar).navigationContentDescription as String
    }
    return description
}*/