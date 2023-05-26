package com.github.ybecker.epforuml.util

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher

class onViewWithTimeout {

    companion object{
        fun onViewWithTimeout(
            matcher: Matcher<View>,
            retryAssertion: ViewAssertion = ViewAssertions.matches(ViewMatchers.isDisplayed())
        ): ViewInteraction {
            repeat(20) { i ->
                try {
                    val viewInteraction = Espresso.onView(matcher)
                    viewInteraction.check(retryAssertion)
                    return viewInteraction
                } catch (e: NoMatchingViewException) {
                    if (i >= 20) {
                        throw e
                    } else {
                        Thread.sleep(200)
                    }
                }
            }
            throw AssertionError("View matcher is broken for $matcher")
        }
    }
}