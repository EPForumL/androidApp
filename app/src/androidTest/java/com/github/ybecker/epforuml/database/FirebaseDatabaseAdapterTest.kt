package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.database.Model.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test


class FirebaseDatabaseAdapterTest {

    private lateinit var database: FirebaseDatabase
    private lateinit var db: FirebaseDatabaseAdapter
    private lateinit var swEng: Course
    private lateinit var sdp: Course
    private lateinit var romain: User
    private lateinit var theo: User
    private lateinit var question1: Question
    private lateinit var question2: Question
    private lateinit var answer1: Answer
    private lateinit var answer2: Answer

    // FirebaseDatabaseAdapter private variables
    private val usersPath = "users"
    private val coursesPath = "courses"
    private val questionsPath = "questions"
    private val answersPath = "answers"
    private val subscriptionsPath = "subscriptions"

    private val courseIdPath = "courseId"
    private val userIdPath = "userId"
    private val questionIdPath = "questionId"
    private val answerIdPath = "answerId"

    private val courseNamePath = "courseName"
    private val usernamePath = "username"

    private val questionTextPath = "questionText"
    private val questionTitlePath = "questionTitle"
    private val answerTextPath = "answerText"

    private val questionURIPath = "imageURI"

    //To run this test make sur to install firebase database emulator
    //and run "firebase emulators:start --only database"
    @Before
    fun setUp() {

        database = Firebase.database

        //local tests works on the emulator but the CI fails
        // so with the try-catch it work but on the real database...
        try{
            //database.useEmulator("10.0.2.2", 9000)
        }
        catch (r : IllegalStateException){ }

        db = FirebaseDatabaseAdapter(database)

        val firebaseDB = database.reference

        firebaseDB.child("courses").setValue(null)
        firebaseDB.child("users").setValue(null)
        firebaseDB.child("questions").setValue(null)
        firebaseDB.child("answers").setValue(null)

        swEng = db.addCourse("SwEng")
        sdp = db.addCourse("SDP")
        db.addCourse("AnalyseI")
        db.addCourse("AnalyseII")
        db.addCourse("AnalyseIII")
        db.addCourse("AnalyseIV")
        db.addCourse("Algo")
        db.addCourse("TOC")
        db.addCourse("POO")
        db.addCourse("POS")
        db.addCourse("OS")
        db.addCourse("Database")

        romain = db.addUser("0", "Romain").get()
        theo = db.addUser("1","Theo").get()

        question1 = db.addQuestion(romain.userId, sdp.courseId, "About SDP", "I have question about the SDP course !","https://media.architecturaldigest.com/photos/5890e88033bd1de9129eab0a/4:3/w_960,h_720,c_limit/Artist-Designed%20Album%20Covers%202.jpg")
        question2 = db.addQuestion(romain.userId, sdp.courseId, "Kotlin", "I think that the lambda with 'it' in Kotlin are great !","https://media.architecturaldigest.com/photos/5890e88033bd1de9129eab0a/4:3/w_960,h_720,c_limit/Artist-Designed%20Album%20Covers%202.jpg")

        answer1 = db.addAnswer(romain.userId, question2.questionId, "Yes they are !")
        answer2 = db.addAnswer(romain.userId, question2.questionId, "The exclamation marks are also really great")

        romain = db.addSubscription(romain.userId, sdp.courseId).get() ?: User("", "error", emptyList(), emptyList(), emptyList())
        romain = db.addSubscription(romain.userId, swEng.courseId).get() ?: User("", "error", emptyList(), emptyList(), emptyList())
        romain = db.addSubscription(romain.userId, swEng.courseId).get() ?: User("", "error", emptyList(), emptyList(), emptyList())
    }

    @Test
    fun addAndGetUser() {
        val user2 = db.addUser("2","TestUser2").get()
        db.getUserById(user2.userId).thenAccept {
            assertThat(it, equalTo(user2))
        }.join()
    }

    @Test
    fun getCourseByIdTest() {
        db.getCourseById(sdp.courseId).thenAccept {
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
        val user = db.addUser(romain.userId, "NewRomain").get()
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

    @Test
    fun addCourse(){
        val newCourseName = "addedCourseTest"
        db.availableCourses().thenAccept {
            assertThat(it.filter{ it.courseId == newCourseName }, equalTo(emptyList()))
        }.join()
        val newCourse = db.addCourse(newCourseName)
        db.availableCourses().thenAccept {
            assertThat(it.filter { it.courseId == newCourse.courseId}, equalTo(listOf(newCourse)))
        }.join()
    }

    @Test
    fun removeUserTest(){
        val newUser = db.addUser("newID", "newNAME").get()
        db.getUserById(newUser.userId).thenAccept {
            assertThat(newUser.username, equalTo(it?.username))
            assertThat(newUser.userId, equalTo(it?.userId))
        }.join()
        db.removeUser(newUser.userId)
        db.getUserById(newUser.userId).thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun removeSubscription(){
        val testCourse = db.addCourse("NEW TEST COURSE")
        val testUser = db.addUser("IDID", "TestUser").get()
        db.addSubscription(testUser.userId, testCourse.courseId)
        db.getUserSubscriptions(testUser.userId).thenAccept {
            it.contains(testCourse)
        }.join()
        db.removeSubscription(testUser.userId, testCourse.courseId)
        db.getUserSubscriptions(testUser.userId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()
    }

    @Test
    fun getMalformedCourseIsNull(){
        val dbRef = database.reference
        val newCourseId = "test"
        db.getCourseById(newCourseId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(coursesPath).child(newCourseId).setValue(null)

        db.getCourseById(newCourseId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(coursesPath).child(newCourseId).child(courseIdPath).setValue(newCourseId)

        db.getCourseById(newCourseId).thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun getMalformedUserIsNull(){
        val dbRef = database.reference
        val newUserId = "test"
        db.getUserById(newUserId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(usersPath).child(newUserId).setValue(null)

        db.getUserById(newUserId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(usersPath).child(newUserId).child(userIdPath).setValue(newUserId)

        db.getUserById(newUserId).thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun getMalformedQuestionIsNull(){
        val dbRef = database.reference
        val newQuestionId = "test"
        db.getQuestionById(newQuestionId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(questionsPath).child(newQuestionId).setValue(null)

        db.getQuestionById(newQuestionId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(questionsPath).child(newQuestionId).child(questionIdPath).setValue(newQuestionId)

        db.getQuestionById(newQuestionId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(questionsPath).child(newQuestionId).child(courseIdPath).setValue("someCourse")

        db.getQuestionById(newQuestionId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(questionsPath).child(newQuestionId).child(userIdPath).setValue("someUser")

        db.getQuestionById(newQuestionId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(questionsPath).child(newQuestionId).child(questionTitlePath).setValue("someTitle")

        db.getQuestionById(newQuestionId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(questionsPath).child(newQuestionId).child(questionTextPath).setValue("someText")

        db.getQuestionById(newQuestionId).thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun getMalformedAnswerIsNull(){
        val dbRef = database.reference
        val newAnswerId = "test"
        db.getCourseById(newAnswerId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(answersPath).child(newAnswerId).setValue(null)

        db.getAnswerById(newAnswerId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(answersPath).child(newAnswerId).child(answerIdPath).setValue(newAnswerId)

        db.getAnswerById(newAnswerId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(answersPath).child(newAnswerId).child(questionIdPath).setValue("someQuestion")

        db.getAnswerById(newAnswerId).thenAccept {
            assertNull(it)
        }.join()
        dbRef.child(answersPath).child(newAnswerId).child(userIdPath).setValue("someUSer")

        db.getAnswerById(newAnswerId).thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun addNewObjectWithTolerateNullArgsTest(){

        val newAnswer = db.addAnswer(romain.userId, question1.questionId, null)
        db.getAnswerById(newAnswer.answerId).thenAccept {
            assertThat(it?.answerText, equalTo(""))
        }

        val newQuestion = db.addQuestion(romain.userId, question1.questionId, "title", null, "URI")
        db.getQuestionById(newQuestion.questionId).thenAccept {
            assertThat(it?.questionText, equalTo(""))
        }
    }
}