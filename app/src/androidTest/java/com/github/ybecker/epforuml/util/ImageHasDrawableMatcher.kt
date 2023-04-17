package com.github.ybecker.epforuml.util

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description

class ImageHasDrawableMatcher {

    companion object {
        fun hasDrawable(drawable: Int): BoundedMatcher<View, ImageButton> {
            return object: BoundedMatcher<View, ImageButton>(ImageButton::class.java) {
                override fun describeTo(description: Description?) {
                    description?.appendText("has correct drawable")
                }

                override fun matchesSafely(item: ImageButton?): Boolean {
                    val image = item?.drawable.toString()
                    return drawable.toString() == image
                }
            }
        }
    }

}