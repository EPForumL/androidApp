package com.github.ybecker.epforuml

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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

                assertThat(db.getQuestions(c)?.get()?.get(0), `is`(question))
            }

        }

    }

    @Test
    fun addAndGetAnswerTest() {
        DatabaseManager.useMockDatabase()

        val db = DatabaseManager.getDatabase()
        val courses = db?.availableCourses()

        val question = "How to write tests in Kotlin?"
        val answer = "Use the Kotlin Documentation !"
        if (courses != null) {
            for (c in courses){
                db.addAnswers(c, question, answer)

                assertThat(db.getAnswers(c, question)?.get()?.get(0), `is`(answer))
            }

        }
    }


}