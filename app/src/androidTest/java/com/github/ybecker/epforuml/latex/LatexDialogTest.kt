package com.github.ybecker.epforuml.latex

import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.`is` as Is

@RunWith(AndroidJUnit4::class)
class LatexDialogTest {
    lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun initScenario() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun closeScenario() {
        scenario.close()
    }

    @Test
    fun checkDialogShowsWithExpectedViews() {
        lateinit var dialog: LatexDialog
        scenario.onActivity {
            dialog = LatexDialog(it, null)
            dialog.show()
        }

        Thread.sleep(1000)

        onView(ViewMatchers.withId(R.id.latex_input))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.latex_editText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.latex_render_button))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.latex_output))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.latex_mathView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.latex_save_button))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.latex_cancel_button))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun checkTextsAreEmptyWhenTargetEditTextIsEmpty() {
        lateinit var dialog: LatexDialog
        lateinit var targetEditText: EditText
        scenario.onActivity {
            targetEditText = EditText(it)
            targetEditText.setText("")
            dialog = LatexDialog(it, targetEditText)
            dialog.show()
        }
        val input = dialog.findViewById<EditText>(R.id.latex_editText)
        assertThat(input.text.toString(), Is(""))

        val rendered = dialog.findViewById<MathView>(R.id.latex_mathView)
        assertThat(rendered.getDisplayText(), Is(""))
    }

    @Test
    fun checkTextsAreNotEmptyWhenTargetEditTextIsNot() {
        lateinit var dialog: LatexDialog
        lateinit var targetEditText: EditText
        val text = "test"
        scenario.onActivity {
            targetEditText = EditText(it)
            targetEditText.setText(text)
            dialog = LatexDialog(it, targetEditText)
            dialog.show()
        }
        val input = dialog.findViewById<EditText>(R.id.latex_editText)
        assertThat(input.text.toString(), Is(text))

        val rendered = dialog.findViewById<MathView>(R.id.latex_mathView)
        assertThat(rendered.getDisplayText(), Is(text))
    }


    // TODO: Check for CI...
    @Test
    fun checkMathViewChangesWhenClickOnRender() {
        lateinit var dialog: LatexDialog
        lateinit var targetEditText: EditText
        val text = "test"
        scenario.onActivity {
            targetEditText = EditText(it)
            targetEditText.setText(text)
            dialog = LatexDialog(it, targetEditText)
            dialog.show()
        }
        val input = dialog.findViewById<EditText>(R.id.latex_editText)
        assertThat(input.text.toString(), Is(text))

        val rendered = dialog.findViewById<MathView>(R.id.latex_mathView)
        assertThat(rendered.getDisplayText(), Is(text))

        val text2 = "test2"

        Thread.sleep(1000)

        onView(ViewMatchers.withId(R.id.latex_editText))
            .perform(ViewActions.click())
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText(text2))
            .perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.latex_render_button))
            .perform(ViewActions.click())

        assertThat(input.text.toString(), Is(text2))
        assertThat(rendered.getDisplayText(), Is(text2))
    }


    //TODO: Find why it fails on CI
    @Test
    fun checkTextIsSavedWhenClickOnSave() {
        lateinit var dialog: LatexDialog
        lateinit var targetEditText: EditText
        val text = "test"
        scenario.onActivity {
            targetEditText = EditText(it)
            targetEditText.setText(text)
            dialog = LatexDialog(it, targetEditText)
            dialog.show()
        }
        val input = dialog.findViewById<EditText>(R.id.latex_editText)
        assertThat(input.text.toString(), Is(text))

        val rendered = dialog.findViewById<MathView>(R.id.latex_mathView)
        assertThat(rendered.getDisplayText(), Is(text))

        Thread.sleep(2000)

        val text2 = "test2"
        onView(ViewMatchers.withId(R.id.latex_editText))
            .perform(ViewActions.click())
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText(text2))
            .perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.latex_render_button))
            .perform(ViewActions.click())

        Thread.sleep(2000)

        assertThat(input.text.toString(), Is(text2))
        assertThat(rendered.getDisplayText(), Is(text2))

        onView(ViewMatchers.withId(R.id.latex_save_button))
            .perform(ViewActions.click())

        Thread.sleep(2000)

        assertThat(targetEditText.text.toString(), Is(text2))

        onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

/*
    @Test
    fun checkTextIsNotSavedWhenClickOnCancel() {
        lateinit var dialog: LatexDialog
        lateinit var targetEditText: EditText
        val text = "test"
        scenario.onActivity {
            targetEditText = EditText(it)
            targetEditText.setText(text)
            dialog = LatexDialog(it, targetEditText)
            dialog.show()
        }
        val input = dialog.findViewById<EditText>(R.id.latex_editText)
        assertThat(input.text.toString(), Is(text))

        val rendered = dialog.findViewById<MathView>(R.id.latex_mathView)
        assertThat(rendered.getDisplayText(), Is(text))

        val text2 = "test2"

        Thread.sleep(1000)

        onView(ViewMatchers.withId(R.id.latex_editText))
            .perform(ViewActions.click())
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText(text2))
            .perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.latex_render_button))
            .perform(ViewActions.click())

        assertThat(input.text.toString(), Is(text2))
        assertThat(rendered.getDisplayText(), Is(text2))

        onView(ViewMatchers.withId(R.id.latex_cancel_button))
            .perform(ViewActions.click())

        assertThat(targetEditText.text.toString(), Is(text))

        onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

 */
}