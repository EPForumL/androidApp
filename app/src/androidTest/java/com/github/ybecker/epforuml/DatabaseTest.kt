package com.github.ybecker.epforuml

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import com.github.ybecker.epforuml.Model.*
import junit.framework.TestCase.assertNull
import org.junit.Before

class MockDatabaseTest {

    @Before
    fun setUp(){
        DatabaseManager.useMockDatabase()
    }

    private val db = DatabaseManager.getDatabase()
    private val user = User("user1", "TestUser1", emptyList())
    private val SwEng = Course("0","Sweng", mutableListOf())
    private val SDP = Course("1","SDP", mutableListOf())

    @Test
    fun addAndGetQuestionTest() {

    }

    @Test
    fun availableCoursesTest() {
        val db = DatabaseManager.getDatabase()

        val courseOfMockDB = listOf(SwEng, SDP)

        assertThat(db?.availableCourses(), `is`(courseOfMockDB))
    }

    @Test
    fun userQuestionNullTest() {
//        val db = DatabaseManager.getDatabase()
//
//        val userQuestions = db?.getUserQuestions(user)
//
//        assertThat(userQuestions).isEqualTo(emptyList())

    }

    @Test
    fun userQuestionListTest() {
        val db = DatabaseManager.getDatabase()

        val user = User("user1", "TestUser1", emptyList())

        val userQuestions = db?.getUserQuestions(user)

        db?.addQuestion(user, SDP, "Should we use Kotlin for Android Development?")
        db?.addQuestion(user, SDP, "We prefer to use XML over Jetpack Compose.")


        assertThat(userQuestions, `is`(mutableListOf()))

    }


}