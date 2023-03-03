package com.github.ybecker.epforuml

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MockDatabaseTest {

    @Test
    fun addAndGetQuestionTest() {
        DatabaseManager.useMockDatabase()

        val db = DatabaseManager.getDatabase()
        val courses = db?.availableCourses()

        val question = "How to write tests in Kotlin?"
        if (courses != null) {
            for (c in courses){
                db.addQuestion(c, question)

                assertThat(db.getQuestions(c), `is`(question))
            }

        }

    }

    @Test
    fun addAndGetAnswerTest() {

    }


}