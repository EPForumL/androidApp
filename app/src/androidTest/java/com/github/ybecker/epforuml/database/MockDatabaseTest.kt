package com.github.ybecker.epforuml.database

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import com.github.ybecker.epforuml.database.Model.*
import junit.framework.TestCase.assertNull
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
        assertThat(db.getUserById(user2.userId), equalTo(user2))
    }

    @Test
    fun getCourseByIdTest(){
        assertThat(db.getCourseById(SDP.courseId), equalTo(SDP))
    }

    @Test
    fun getUnexistingCourseByIdReturnsNullTest(){
        assertNull(db?.getCourseById("nothing"))
    }

    @Test
    fun getUserByIdTest(){
        assertThat(db?.getUserById(user.userId), equalTo(user))
    }

    @Test
    fun getUnexistingUserByIdReturnsNullTest(){
        assertNull(db.getUserById("nobody"))
    }

    @Test
    fun AddAndGetQuestionByIdTest(){
        val question = db.addQuestion(user, SDP, "I have a question.")
        assertThat(db.getQuestionById(question.questionId), equalTo(question))
    }

    @Test
    fun getUnexistingQuestionByIdReturnsNullTest(){
        assertNull(db.getQuestionById("nothing"))
    }

    @Test
    fun AddAndGetAnswerByIdTest(){
        val question = db.addQuestion(user, SDP, "I have a question.")
        val answer = db.addAnswer(user, question, "And what is it ?")
        assertThat(db.getQuestionById(question.questionId), equalTo(question))
        assertThat(db.getAnswerById(answer.answerId), equalTo(answer))
    }

    @Test
    fun getUnexistingAnswerByIdReturnsNullTest(){
        assertNull(db.getAnswerById("nothing"))
    }

    @Test
    fun availableCoursesTest() {
        val courseOfMockDB = setOf(SDP, SwEng)
        assertThat(db.availableCourses(), equalTo(courseOfMockDB))
    }

    /*@Test
    fun userAndCourseQuestionListTest() {
        val userQuestionsBefore = db.getUserQuestions(user)

        val q1 = db.addQuestion(user, SDP, "Should we use Kotlin for Android Development?")
        val q2 = db.addQuestion(user, SDP, "We prefer to use XML over Jetpack Compose.")
        val userQuestionsAfter = db.getUserQuestions(user)

        assertThat(userQuestionsBefore, equalTo(emptySet()))
        assertThat(userQuestionsAfter, equalTo(setOf(q2, q1)))
        assertThat(userQuestionsAfter, equalTo(db.getCourseQuestions(SDP) + 3))
    }*/

    @Test
    fun getAnswerFromQuestionTest(){

        val q1 = db.addQuestion(user, SDP, "Should we use Kotlin for Android Development?")
        val a1 = db.addAnswer(user, q1, "Yes, it is well documented on the internet")
        val a2= db.addAnswer(user, q1, "Yes it is.")

        val answers = setOf(a2, a1)
        assertThat(db.getQuestionAnswers(q1), equalTo(answers))
        assertThat(db.getUserAnswers(user), equalTo(answers))

    }

    @Test
    fun getAnswerFromQuestionWithoutAnyAnswerTest(){
        val q2 = db.addQuestion(user, SDP, "We prefer to use XML over Jetpack Compose.")
        assertThat(db.getQuestionAnswers(q2), equalTo(setOf()))
    }

    @Test
    fun getUserSubscriptionTest(){
        user = db.addSubscription(user, SwEng) ?: User("", "error", emptyList(), emptyList(), emptyList())
        user = db.addSubscription(user, SDP) ?: User("", "error", emptyList(), emptyList(), emptyList())
        user = db.addSubscription(user, SDP) ?: User("", "error", emptyList(), emptyList(), emptyList())
        assertThat(user.subscriptions.map { it.courseId }, equalTo(db.getUserSubscriptions(user).map { it.courseId }))
        assertThat(user.subscriptions.map { it.courseId }, equalTo(setOf(SwEng, SDP).map { it.courseId }))
    }
}