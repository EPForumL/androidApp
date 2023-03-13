package com.github.ybecker.epforuml.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.database.Model.*
import com.google.firebase.database.FirebaseDatabase
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith


class FirebaseDatabaseAdapterTest {

    private lateinit var db: Database
    private lateinit var swEng: Course
    private lateinit var sdp: Course
    private lateinit var romain: User
    private lateinit var theo: User
    private lateinit var question1: Question
    private lateinit var question2: Question
    private lateinit var answer1: Answer
    private lateinit var answer2: Answer

    companion object{
        @BeforeClass
        @JvmStatic
        fun emulatorSetup(){
            //To run this test make sur to install firebase database emulator
            //and run "firebase emulators:start --only database"

            val firebaseInstance = FirebaseDatabase
                .getInstance("https://epforuml-38150-default-rtdb.europe-west1.firebasedatabase.app")
                .useEmulator("127.0.0.1", 9000)
        }
    }

    @Before
        fun setUp() {

            val firebaseDB = FirebaseDatabase.getInstance("https://epforuml-38150-default-rtdb.europe-west1.firebasedatabase.app").reference

            firebaseDB.child("courses").setValue(null)
            firebaseDB.child("users").setValue(null)
            firebaseDB.child("questions").setValue(null)
            firebaseDB.child("answers").setValue(null)

            db = DatabaseManager.getDatabase()

            swEng = Course("0", "SwEng", emptyList())
            sdp = Course("1", "SDP", emptyList())

            firebaseDB.child("courses").child("0").setValue(swEng)
            firebaseDB.child("courses").child("1").setValue(sdp)

            romain = db.addUser("0", "Romain")
            theo = db.addUser("1","Theo")

            question1 = db.addQuestion(romain, sdp, "I have question about the SDP course !")
            question2 = db.addQuestion(romain, sdp, "I think that the lambda with 'it' in Kotlin are great !")

            answer1 = db.addAnswer(romain, question2, "Yes they are !")
            answer2 = db.addAnswer(romain, question2, "The exclamation marks are also really great")

            romain = db.addSubscription(romain, sdp) ?: User("", "error", emptyList(), emptyList(), emptyList())
            romain = db.addSubscription(romain, swEng) ?: User("", "error", emptyList(), emptyList(), emptyList())

        }


    @Test
    fun addAndGetUser() {
        val user2 = db.addUser("2","TestUser2")
        assertThat(db.getUserById(user2.userId), equalTo(user2))
    }

    @Test
    fun getCourseByIdTest() {
        assertThat(db.getCourseById("1")?.courseId, equalTo(sdp.courseId))
    }

    @Test
    fun getUnexistingCourseByIdReturnsNullTest() {
        assertNull(db.getCourseById("nothing"))
    }

    @Test
    fun getUserByIdTest() {
        assertThat(db.getUserById(romain.userId)?.userId, equalTo(romain.userId))
        assertThat(db.getUserById(romain.userId)?.username, equalTo(romain.username))
    }

    @Test
    fun getUnexistingUserByIdReturnsNullTest() {
        assertNull(db.getUserById("nobody"))
    }

    @Test
    fun addAndgetQuestionByIdTest() {
        val question = db.addQuestion(romain, sdp, "I have a question.")
        assertThat(db.getQuestionById(question.questionId), equalTo(question))
    }

    @Test
    fun getUnexistingQuestionByIdReturnsNullTest(){
        assertNull(db.getQuestionById("nothing"))
    }

    @Test
    fun getAnswerByIdTest(){
        assertThat(db.getAnswerById(answer1.answerId), equalTo(answer1))
    }

    @Test
    fun getUnexistingAnswerByIdReturnsNullTest(){
        assertNull(db.getAnswerById("nothing"))
    }

    @Test
    fun availableCoursesTest() {
        assertTrue(db.availableCourses().map { it.courseId }.containsAll(listOf(swEng.courseId, sdp.courseId)))
    }

    @Test
    fun getCourseQuestionTest(){
        assertThat(db.getCourseQuestions(sdp).map { it.questionId }.toSet(), equalTo(setOf(question1.questionId, question2.questionId)))
    }

    @Test
    fun getCourseQuestionWhenEmpty(){
        assertThat(db.getCourseQuestions(swEng).map { it.questionId }.toSet(), equalTo(setOf()))
    }

    @Test
    fun getUserQuestionTest(){
        assertThat(db.getUserQuestions(romain).map { it.questionId }.toSet(), equalTo(setOf(question1.questionId, question2.questionId)))
    }

    @Test
    fun getUserQuestionWhenEmpty(){
        assertThat(db.getUserQuestions(theo).map { it.questionId }.toSet(), equalTo(setOf()))
    }

    @Test
    fun getUserAnswersTest(){
        assertThat(db.getUserAnswers(romain).map { it.answerId }.toSet(), equalTo(setOf(answer1.answerId,answer2.answerId)))
    }

    @Test
    fun getUserAnswersWhenEmpty(){
        assertThat(db.getUserAnswers(theo).map { it.answerId }.toSet(), equalTo(setOf()))
    }

    @Test
    fun getQuestionAnswers(){
        assertThat(db.getQuestionAnswers(question2).map { it.answerId }.toSet(), equalTo(setOf(answer1.answerId, answer2.answerId)))
    }

    @Test
    fun getQuestionAnswersWhenEmpty(){
        assertThat(db.getQuestionAnswers(question1).map { it.answerId }.toSet(), equalTo(setOf()))
    }

    @Test
    fun addExistingUserReturnTheUserTest(){
        val user = db.addUser(romain.userId, "NewRomain")
        assertThat(user.username, equalTo(romain.username))
    }

    @Test
    fun getUserSubscriptionTest(){
        assertThat(romain.subscriptions.map { it.courseId }, equalTo(db.getUserSubscriptions(romain).map { it.courseId }))
        assertThat(romain.subscriptions.map { it.courseId }, equalTo(setOf(sdp, swEng).map { it.courseId }))
    }


}