package com.github.ybecker.epforuml

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.database.DatabaseManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AnswerAdapterTest {

    private lateinit var scenario : ActivityScenario<QuestionDetailsActivity>

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()
        scenario = ActivityScenario.launch(QuestionDetailsActivity::class.java)
    }

    @Test
    fun questionWithNoAnswerDisplaysNoAnswer() {

        Espresso.onView(ViewMatchers.withId(R.id.qdetails_answer_text))
    }

    @After
    fun closing() {
        scenario.close()
    }

}