package com.github.ybecker.epforuml.util

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import androidx.core.graphics.drawable.toBitmap
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description

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