package com.github.ybecker.epforuml.database

import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import com.github.ybecker.epforuml.database.Model.*
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import java.time.LocalDateTime

class MockDatabaseTest {

    private lateinit var db: Database
    private var swEng = Course("course0","Sweng", emptyList())
    private var sdp = Course("course1","SDP", emptyList())
    private lateinit var user: User
    private lateinit var nullUser: User
    private lateinit var question1: Question
    private lateinit var question2: Question
    private lateinit var question3: Question
    private lateinit var answer1: Answer
    private lateinit var answer2: Answer

    @Before
    fun setUp(){
        DatabaseManager.useMockDatabase()
        db = DatabaseManager.getDatabase()

        user = db.addUser("0","TestUser", "testEmail").get()
        nullUser = db.addUser("1","nullUser", "testEmail").get()

        question1 = db.addQuestion(user.userId, sdp.courseId, "Question about Cirrus CI", "How do I fix the CI ?", "https://media.architecturaldigest.com/photos/5890e88033bd1de9129eab0a/4:3/w_960,h_720,c_limit/Artist-Designed%20Album%20Covers%202.jpg")
        question2 = db.addQuestion(user.userId, sdp.courseId, "About the Scrum master", "What is the exact role of a Scrum Master ?", "")

        question3 = db.addQuestion(user.userId, sdp.courseId, "Very long question",
            "Extremely long long long long long long long long long long long long long " +
                    "long long long long long long long long long long long long long long long" +
                    "long long long long long long long long long long long long long long long" +
                    "long long long long long long long long long long long long long long long " +
                    "question" ,"")

        answer1 = db.addAnswer(user.userId, question1.questionId, "Try to re-run the CI.")
        answer2 = db.addAnswer(user.userId, question1.questionId, "I have already tried it :(")
        db.addAnswer(user.userId, question1.questionId, "I am talking alone actually")

        db.addSubscription(user.userId, swEng.courseId)
        db.addSubscription(user.userId, sdp.courseId)

        val chat2 = Chat("chat0", LocalDateTime.now().toString(), user.userId, user.userId, "Hey me!")

        db.addChat(chat2.senderId, chat2.receiverId, chat2.text)
    }

    @Test
    fun addAndGetUser(){
        val user2 = db.addUser("user2", "TestUser2", "testEmail").get()
        db.getUserById(user2.userId).thenAccept {
            assertThat(it, equalTo(user2))
        }.join()
    }
    @Test
    fun addAndGetChat(){
        db.addChat("0", "0", "hey")
        db.getChat(user.userId,user.userId).thenAccept {
            assertThat(it.size, equalTo(2))
        }.join()

    }
    @Test
    fun addAndGetChatWith(){
        db.addChatsWith("0", "0")
        assertThat( db.getUserById("0").get()?.chatsWith!!.size,equalTo(1))
    }

    @Test
    fun getIdByName(){
        assertThat( db.getUserId("TestUser").get(),equalTo("0"))
    }

    @Test
    fun getCourseByIdTest(){
        db.getCourseById(sdp.courseId).thenAccept {
            assertThat(it?.courseId, equalTo(sdp.courseId))
            assertThat(it?.courseName, equalTo(sdp.courseName))
        }.join()
    }

    @Test
    fun retrieveRegisteredUsers(){
        val user2 = db.addUser("user2", "TestUser2", "testEmail").get()
        val testUser = db.addUser("IDID", "TestUser", "testEmail").get()

        assertThat( db.registeredUsers().get().size, equalTo(6))
    }

    @Test
    fun getUnexistingCourseByIdReturnsNullTest(){
        db.getCourseById("nothing").thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun getUserByIdTest(){
        db.getUserById(user.userId).thenAccept {
            assertThat(it?.userId, equalTo(user.userId))
            assertThat(it?.username, equalTo(user.username))
        }.join()
    }

    @Test
    fun getUnexistingUserByIdReturnsNullTest(){
        db.getUserById("nobody").thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun AddAndGetQuestionByIdTest(){
        val question = db.addQuestion(user.userId, sdp.courseId, "Question","I have a question.", "")
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
    fun AddAndGetAnswerByIdTest(){
        val question = db.addQuestion(user.userId, sdp.courseId, "Question","I have a question.", "")
        val answer = db.addAnswer(user.userId, question.questionId, "And what is it ?")

        db.getQuestionById(question.questionId).thenAccept {
            assertThat(it, equalTo(question))
        }.join()
        db.getAnswerById(answer.answerId).thenAccept {
            assertThat(it, equalTo(answer))
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
        val courseOfMockDB = listOf(swEng, sdp)
        db.availableCourses().thenAccept {
            assertTrue(it.map { it.courseId }.containsAll(courseOfMockDB.map { it.courseId }))
        }.join()
    }

    @Test
    fun getAnswerFromQuestionTest(){

        val q1 = db.addQuestion(user.userId, sdp.courseId, "Kotlin","Should we use Kotlin for Android Development?","")

        db.getQuestionAnswers(q1.questionId).thenAccept {
            assertThat(it, equalTo(emptyList()))
        }.join()

        val a1 = db.addAnswer(user.userId, q1.questionId, "Yes, it is well documented on the internet")
        val a2= db.addAnswer(user.userId, q1.questionId, "Yes it is.")
        val answers = listOf(a1, a2)

        db.getUserAnswers(user.userId).thenAccept {
            assertTrue(it.containsAll(answers))
        }.join()
    }

    @Test
    fun getAnswerFromQuestionWithoutAnyAnswerTest(){
        val q2 = db.addQuestion(user.userId, sdp.courseId, "XML vs JetpackCompose","We prefer to use XML over Jetpack Compose.","")
        db.getQuestionAnswers(q2.questionId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()
    }

    @Test
    fun getUserSubscriptionTest(){
        db.addSubscription(user.userId, swEng.courseId)
        db.addSubscription(user.userId, sdp.courseId)
        db.addSubscription(user.userId, sdp.courseId)
        db.getUserSubscriptions(user.userId).thenAccept {
            assertThat(it.map { it.courseId }, equalTo(listOf(swEng, sdp).map { it.courseId }))
        }.join()
    }

    @Test
    fun getQuestionTitleTest(){
        val q = db.addQuestion(user.userId, sdp.courseId, "chatGPT","He is a friend of mine :)","")
        db.getQuestionById(q.questionId).thenAccept {
            assertThat(it?.questionTitle, equalTo(q.questionTitle))
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
        val newUser = db.addUser("newID", "newNAME", "testEmail").get()
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
        val testUser = db.addUser("IDID", "TestUser", "testEmail").get()
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
    fun getUserQuestionTest(){
        db.getUserQuestions(user.userId).thenAccept {
            assertThat(it.map { it.questionId }, equalTo(listOf(question1.questionId, question2.questionId, question3.questionId)))
        }.join()
    }

    @Test
    fun getUserSubscriptionsWithNullUserTest(){
        db.addSubscription("nobody", sdp.courseId).thenAccept {
            assertNull(it)
        }
    }

    @Test
    fun addExistingUserReturnOriginalTest(){
        db.addUser(user.userId, "NEWUSER", user.email).thenAccept {
            assertThat(it?.username, equalTo(user.username))
        }
    }

    @Test
    fun addNewObjectWithTolerateNullArgsTest(){

        val newAnswer = db.addAnswer(user.userId, question1.questionId, null)
        db.getAnswerById(newAnswer.answerId).thenAccept {
            assertThat(it?.answerText, equalTo(""))
        }

        val newQuestion = db.addQuestion(user.userId, question1.questionId, "title", null, "URI")
        db.getQuestionById(newQuestion.questionId).thenAccept {
            assertThat(it?.questionText, equalTo(""))
        }
    }
}