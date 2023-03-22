package com.github.ybecker.epforuml.database

import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import com.github.ybecker.epforuml.database.Model.*
import junit.framework.TestCase
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before

class MockDatabaseTest {

    private var db: Database = DatabaseManager.getDatabase()
    // these variable are already in the MockDatabase
    private var user = User("user1", "TestUser", emptyList(), emptyList(), emptyList())
    private val SwEng = Course("course0","Sweng", emptyList())
    private val SDP = Course("course1","SDP", emptyList())

    @Before
    fun setUp(){
        DatabaseManager.useMockDatabase()
        db = DatabaseManager.getDatabase()
    }

    @Test
    fun addAndGetUser(){
        val user2 = User("user2", "TestUser2", emptyList(), emptyList(), emptyList())
        db.addUser(user2.userId, user2.username)
        db.getUserById(user2.userId).thenAccept {
            assertThat(db.getUserById(user2.userId), equalTo(user2))
        }
    }

    @Test
    fun getCourseByIdTest(){
        db.getCourseById(SDP.courseId).thenAccept {
            assertThat(it, equalTo(SDP))
        }
    }

    @Test
    fun getUnexistingCourseByIdReturnsNullTest(){
        db.getCourseById("nothing").thenAccept {
            assertNull(it)
        }
    }

    @Test
    fun getUserByIdTest(){
        db.getUserById(user.userId).thenAccept {
            assertThat(it?.userId, equalTo(user.userId))
            assertThat(it?.username, equalTo(user.username))
        }
    }

    @Test
    fun getUnexistingUserByIdReturnsNullTest(){
        db.getUserById("nobody").thenAccept {
            assertNull(it)
        }
    }

    @Test
    fun AddAndGetQuestionByIdTest(){
        val question = db.addQuestion(user.userId, SDP.courseId, "Question","I have a question.")
        db.getQuestionById(question.questionId).thenAccept {
            assertThat(it, equalTo(question))
        }
    }

    @Test
    fun getUnexistingQuestionByIdReturnsNullTest(){
        db.getQuestionById("nothing").thenAccept {
            assertNull(it)
        }
    }

    @Test
    fun AddAndGetAnswerByIdTest(){
        val question = db.addQuestion(user.userId, SDP.courseId, "Question","I have a question.")
        val answer = db.addAnswer(user.userId, question.questionId, "And what is it ?")

        db.getQuestionById(question.questionId).thenAccept {
            assertThat(it, equalTo(question))
        }
        db.getAnswerById(answer.answerId).thenAccept {
            assertThat(it, equalTo(answer))
        }
    }

    @Test
    fun getUnexistingAnswerByIdReturnsNullTest(){
        db.getAnswerById("nothing").thenAccept {
            assertNull(it)
        }
    }

    @Test
    fun availableCoursesTest() {
        val courseOfMockDB = listOf(SDP, SwEng)
        db.availableCourses().thenAccept {
            assertTrue(it.map { it.courseId }.containsAll(courseOfMockDB.map { it.courseId }))
        }
    }

    @Test
    fun getAnswerFromQuestionTest(){

        val q1 = db.addQuestion(user.userId, SDP.courseId, "Kotlin","Should we use Kotlin for Android Development?")
        val a1 = db.addAnswer(user.userId, q1.questionId, "Yes, it is well documented on the internet")
        val a2= db.addAnswer(user.userId, q1.questionId, "Yes it is.")

        val answers = listOf(a2, a1)
        db.getQuestionAnswers(q1.questionId).thenAccept {
            assertThat(it, equalTo(answers))
        }
        db.getUserAnswers(user.userId).thenAccept {
            assertThat(it, equalTo(answers))
        }
    }

    @Test
    fun getAnswerFromQuestionWithoutAnyAnswerTest(){
        val q2 = db.addQuestion(user.userId, SDP.courseId, "XML vs JetpackCompose","We prefer to use XML over Jetpack Compose.")
        db.getQuestionAnswers(q2.questionId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }
    }

    @Test
    fun getUserSubscriptionTest(){
        db.addSubscription(user.userId, SwEng.courseId)
        db.addSubscription(user.userId, SDP.courseId)
        db.addSubscription(user.userId, SDP.courseId)
        db.getUserSubscriptions(user.userId).thenAccept {
            assertThat(it.map { it.courseId }, equalTo(listOf(SwEng, SDP).map { it.courseId }))
        }
    }

    @Test
    fun getQuestionTitleTest(){
        val q = db.addQuestion(user.userId, SDP.courseId, "chatGPT","He is a friend on mine :)")
        db.getQuestionById(q.questionId).thenAccept {
            assertThat(it?.questionTitle, equalTo(q.questionTitle))
        }
    }
}