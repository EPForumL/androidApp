package com.github.ybecker.epforuml.util

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import androidx.core.graphics.drawable.toBitmap
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher

class ImageButtonHasDrawableMatcher {

    companion object {
        fun hasDrawable(drawable: Int): BoundedMatcher<View, ImageButton> {
            return object: BoundedMatcher<View, ImageButton>(ImageButton::class.java) {
                override fun describeTo(description: Description?) {
                    description?.appendText("has correct drawable")
                }

                override fun matchesSafely(item: ImageButton?): Boolean {
                    val wantedImage = item!!.context!!.resources!!.getDrawable(drawable).toBitmap(24,24)
                    val image = item.background.toBitmap(24,24)

                    return image.sameAs(wantedImage)
                }
            }
        }
    }

}