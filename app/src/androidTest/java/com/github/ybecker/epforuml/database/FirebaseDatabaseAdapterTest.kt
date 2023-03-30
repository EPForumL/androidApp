package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.NewQuestionFragment
import com.github.ybecker.epforuml.database.Model.*
import com.google.firebase.database.FirebaseDatabase
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test


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

    /*companion object{
        @BeforeClass
        @JvmStatic
        fun emulatorSetup(){
            //To run this test make sur to install firebase database emulator
            //and run "firebase emulators:start --only database"

            val firebaseInstance = FirebaseDatabase
                .getInstance("https://epforuml-38150-default-rtdb.europe-west1.firebasedatabase.app")
                //.useEmulator("127.0.0.1", 9000)
        }
    }*/

    @Before
    fun setUp() {

        val firebaseDB = FirebaseDatabase.getInstance("https://epforuml-38150-default-rtdb.europe-west1.firebasedatabase.app").reference

        firebaseDB.child("courses").setValue(null)
        firebaseDB.child("users").setValue(null)
        firebaseDB.child("questions").setValue(null)
        firebaseDB.child("answers").setValue(null)

        db = FirebaseDatabaseAdapter()

        swEng = Course("0", "SwEng", emptyList())
        sdp = Course("1", "SDP", emptyList())

        firebaseDB.child("courses").child("0").setValue(swEng)
        firebaseDB.child("courses").child("1").setValue(sdp)

        val course3 = Course("course2","AnalyseI", mutableListOf())
        firebaseDB.child("courses").child(course3.courseId).setValue(course3)
        val course4 = Course("course3","AnalyseII", mutableListOf())
        firebaseDB.child("courses").child(course4.courseId).setValue(course4)
        val course5 = Course("course4","AnalyseIII", mutableListOf())
        firebaseDB.child("courses").child(course5.courseId).setValue(course5)
        val course6 = Course("course5","AnalyseIV", mutableListOf())
        firebaseDB.child("courses").child(course6.courseId).setValue(course6)
        val course7 = Course("course6","Algo", mutableListOf())
        firebaseDB.child("courses").child(course7.courseId).setValue(course7)
        val course8 = Course("course7","TOC", mutableListOf())
        firebaseDB.child("courses").child(course8.courseId).setValue(course8)
        val course9 = Course("course8","POO", mutableListOf())
        firebaseDB.child("courses").child(course9.courseId).setValue(course9)
        val course10 = Course("course9","POS", mutableListOf())
        firebaseDB.child("courses").child(course10.courseId).setValue(course10)
        val course11 = Course("course10","OS", mutableListOf())
        firebaseDB.child("courses").child(course11.courseId).setValue(course11)
        val course12 = Course("course11","Database", mutableListOf())
        firebaseDB.child("courses").child(course12.courseId).setValue(course12)

        romain = db.addUser("0", "Romain", "testEmail1").get()
        theo = db.addUser("1","Theo", "testEmail2").get()

        question1 = db.addQuestion(romain.userId, sdp.courseId, "About SDP", "I have question about the SDP course !","https://media.architecturaldigest.com/photos/5890e88033bd1de9129eab0a/4:3/w_960,h_720,c_limit/Artist-Designed%20Album%20Covers%202.jpg")
        question2 = db.addQuestion(romain.userId, sdp.courseId, "Kotlin", "I think that the lambda with 'it' in Kotlin are great !","https://media.architecturaldigest.com/photos/5890e88033bd1de9129eab0a/4:3/w_960,h_720,c_limit/Artist-Designed%20Album%20Covers%202.jpg")

        answer1 = db.addAnswer(romain.userId, question2.questionId, "Yes they are !")
        answer2 = db.addAnswer(romain.userId, question2.questionId, "The exclamation marks are also really great")

        romain = db.addSubscription(romain.userId, sdp.courseId).get() ?: User("", "error", "")
        romain = db.addSubscription(romain.userId, swEng.courseId).get() ?: User("", "error", "")
        romain = db.addSubscription(romain.userId, swEng.courseId).get() ?: User("", "error", "")

    }


    @Test
    fun addAndGetUser() {
        val user2 = db.addUser("2","TestUser2", "testEmail").get()
        db.getUserById(user2.userId).thenAccept {
            assertThat(it, equalTo(user2))
        }.join()
    }

    @Test
    fun getCourseByIdTest() {
        db.getCourseById("1").thenAccept {
            assertThat(it?.courseId, equalTo(sdp.courseId))
        }.join()
    }

    @Test
    fun getUnexistingCourseByIdReturnsNullTest() {
        db.getCourseById("nothing").thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun getUserByIdTest() {
        db.getUserById(romain.userId).thenAccept {
            assertThat(it?.userId, equalTo(romain.userId))
        }.join()
        db.getUserById(romain.userId).thenAccept {
            assertThat(it?.username, equalTo(romain.username))
        }.join()
    }

    @Test
    fun getUnexistingUserByIdReturnsNullTest() {
        db.getCourseById("nobody").thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun addAndgetQuestionByIdTest() {
        val question = db.addQuestion(romain.userId, sdp.courseId, "Question","I have a question.", "https://media.architecturaldigest.com/photos/5890e88033bd1de9129eab0a/4:3/w_960,h_720,c_limit/Artist-Designed%20Album%20Covers%202.jpg")
        db.getQuestionById(question.questionId).thenAccept {
            assertThat(it, equalTo(question))
        }.join()
    }

    @Test
    fun getUnexistingQuestionByIdReturnsNullTest(){
        db.getQuestionById("nothing").thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun getAnswerByIdTest(){
        db.getAnswerById(answer1.answerId).thenAccept {
            assertThat(it, equalTo(answer1))
        }.join()
    }

    @Test
    fun getUnexistingAnswerByIdReturnsNullTest(){
        db.getAnswerById("nothing").thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun availableCoursesTest() {
        db.availableCourses().thenAccept {
            assertTrue(it.map { it.courseId }.containsAll(listOf(swEng.courseId, sdp.courseId)))
        }.join()
    }

    @Test
    fun getCourseQuestionTest(){
        db.getCourseQuestions(sdp.courseId).thenAccept {
            assertThat(it. map { it.questionId }, equalTo(listOf(question1.questionId, question2.questionId)))
        }.join()
    }

    @Test
    fun getCourseQuestionWhenEmpty(){
        db.getCourseQuestions(swEng.courseId).thenAccept {
            assertThat(it.map { it.questionId }, equalTo(listOf()))
        }.join()
    }

    @Test
    fun getUserQuestionTest(){
        db.getUserQuestions(romain.userId).thenAccept {
            assertThat(it.map { it.questionId }, equalTo(listOf(question1.questionId, question2.questionId)))
        }.join()
    }

    @Test
    fun getUserQuestionWhenEmpty(){
        db.getUserQuestions(theo.userId).thenAccept {
            assertThat(it.map { it.questionId }, equalTo(listOf()))
        }.join()
    }

    @Test
    fun getUserAnswersTest(){
        db.getUserAnswers(romain.userId).thenAccept {
            assertThat(it.map { it.answerId }, equalTo(listOf(answer1.answerId, answer2.answerId)))
        }.join()
    }

    @Test
    fun getUserAnswersWhenEmpty(){
        db.getUserAnswers(theo.userId).thenAccept {
            assertThat(it.map { it.answerId }, equalTo(listOf()))
        }.join()
    }

    @Test
    fun getQuestionAnswers(){
        db.getQuestionAnswers(question2.questionId).thenAccept {
            assertThat(it.map { it.answerId }, equalTo(listOf(answer1.answerId, answer2.answerId)))
        }.join()
    }

    @Test
    fun getQuestionAnswersWhenEmpty(){
        db.getQuestionAnswers(question1.questionId).thenAccept {
            assertThat(it.map { it.answerId }, equalTo(listOf()))
        }.join()
    }

    @Test
    fun addExistingUserReturnTheUserTest(){
        val user = db.addUser(romain.userId, "NewRomain", romain.address).get()
        assertThat(user.username, equalTo(romain.username))
    }

    @Test
    fun getUserSubscriptionTest(){
        db.getUserSubscriptions(romain.userId).thenAccept {
            assertThat(it.map { it.courseId }, equalTo(listOf(swEng, sdp).map { it.courseId }))
        }.join()

    }

    @Test
    fun getQuestionTitleTest(){
        db.getQuestionById(question1.questionId).thenAccept {
            assertThat(it?.questionTitle, equalTo(question1.questionTitle))
        }.join()
    }

}