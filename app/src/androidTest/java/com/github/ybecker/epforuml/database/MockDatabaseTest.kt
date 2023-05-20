package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.UserStatus
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import com.github.ybecker.epforuml.database.Model.*
import com.google.firebase.messaging.FirebaseMessaging
import junit.framework.TestCase.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

class MockDatabaseTest {

    private lateinit var db: Database
    private var swEng = Course("course0","Sweng", emptyList(), emptyList())
    private var sdp = Course("course1","SDP", emptyList(), emptyList())
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

        db.addQuestion(user.userId, sdp.courseId,false, "Question about Cirrus CI", "How do I fix the CI ?", "https://media.architecturaldigest.com/photos/5890e88033bd1de9129eab0a/4:3/w_960,h_720,c_limit/Artist-Designed%20Album%20Covers%202.jpg","").thenAccept{
            question1 = it
        }
        db.addQuestion(user.userId, sdp.courseId,false, "About the Scrum master", "What is the exact role of a Scrum Master ?", "","").thenAccept {

            question2 = it
        }
        db.addQuestion(user.userId, sdp.courseId, false, "Very long question",
                "Extremely long long long long long long long long long long long long long " +
                        "long long long long long long long long long long long long long long long" +
                        "long long long long long long long long long long long long long long long" +
                        "long long long long long long long long long long long long long long long " +
                        "question" ,"","").thenAccept{
            question3 =it }

        answer1 = db.addAnswer(user.userId, question1.questionId, "Try to re-run the CI.")
        answer2 = db.addAnswer(user.userId, question1.questionId, "I have already tried it :(")
        db.addAnswer(user.userId, question1.questionId, "I am talking alone actually")
        db.addSubscription(user.userId, swEng.courseId)
        db.addSubscription(user.userId, sdp.courseId)

            val chat2 = Chat("chat0", LocalDateTime.now().toString(), user.userId, user.userId, "Hey me!")

            db.addChat(chat2.senderId, chat2.receiverId, chat2.text)
        }


    @Test
    fun getQuestionsTest() {
        val questions = db.getQuestions().get()
        assertEquals(6, questions.size)
        assertTrue(questions.contains(question1))
        assertTrue(questions.contains(question2))
        assertTrue(questions.contains(question3))
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
        val question = db.addQuestion(user.userId, sdp.courseId, false,"Question","I have a question.", "","")

        question.thenAccept{ q->
        db.getQuestionById(q.questionId).thenAccept {
            assertThat(it, equalTo(question))
        }.join()
        }
    }

    @Test
    fun getUnexistingQuestionByIdReturnsNullTest(){
        db.getQuestionById("nothing").thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun AddAndGetAnswerByIdTest(){
        val question = db.addQuestion(user.userId, sdp.courseId,false, "Question","I have a question.", "","")



        question.thenAccept{ q->
            val answer = db.addAnswer(user.userId, q.questionId, "And what is it ?")
            db.getQuestionById(q.questionId).thenAccept {
                assertThat(it, equalTo(question))
            }.join()
            db.getAnswerById(answer.answerId).thenAccept {
                assertThat(it, equalTo(answer))
            }.join()
        }

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

        val q1 = db.addQuestion(user.userId, sdp.courseId,false, "Kotlin","Should we use Kotlin for Android Development?","","")

        q1.thenAccept{ q->

            db.getQuestionAnswers(q.questionId).thenAccept {
                assertThat(it, equalTo(emptyList()))
            }.join()

            val a1 = db.addAnswer(user.userId, q.questionId, "Yes, it is well documented on the internet")
            val a2= db.addAnswer(user.userId, q.questionId, "Yes it is.")
            val answers = listOf(a1, a2)

            db.getUserAnswers(user.userId).thenAccept {
                assertTrue(it.containsAll(answers))
            }.join()
        }
    }

    @Test
    fun getAnswerFromQuestionWithoutAnyAnswerTest(){
        val q2 = db.addQuestion(user.userId, sdp.courseId,false, "XML vs JetpackCompose","We prefer to use XML over Jetpack Compose.","","")

        q2.thenAccept{ q->
            db.getQuestionAnswers(q.questionId).thenAccept {
                assertThat(it, equalTo(listOf()))
            }.join()
        }
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
        val q = db.addQuestion(user.userId, sdp.courseId, false,"chatGPT","He is a friend of mine :)","","")
        q.thenAccept{ q->
            db.getQuestionById(q.questionId).thenAccept {
                assertThat(it?.questionTitle, equalTo(q.questionTitle))
            }.join()
        }
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

        val newQuestion = db.addQuestion(user.userId, question1.questionId, false,"title", null, "URI","")

        newQuestion.thenAccept{ q->
            db.getQuestionById(q.questionId).thenAccept {
                assertThat(it?.questionText, equalTo(""))
            }}
    }

    @Test
    fun addAndGetNewQuestionFollowersTest(){
        db.getQuestionFollowers(question1.questionId).thenAccept {
            assertThat(it, equalTo(emptyList()))
        }.join()
        db.addQuestionFollower(user.userId, question1.questionId)
        db.getQuestionFollowers(question1.questionId).thenAccept {
            assertThat(it, equalTo(listOf(user.userId)))
        }.join()
    }

    @Test
    fun addAndGetNewAnswerLikeTest(){
        db.getAnswerLike(answer1.answerId).thenAccept {
            assertThat(it, equalTo(emptyList()))
        }.join()
        db.addAnswerLike(user.userId, answer1.answerId)
        db.getAnswerLike(answer1.answerId).thenAccept {
            assertThat(it, equalTo(listOf(user.userId)))
        }.join()
    }

    @Test
    fun removeAnswerLikeTest(){

        db.addAnswerLike(user.userId, answer1.answerId)

        db.getAnswerLike(answer1.answerId).thenAccept {
            assertThat(it, equalTo(listOf(user.userId)))
        }.join()

        db.removeAnswerLike(user.userId, answer1.answerId)

        db.getAnswerLike(answer1.answerId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()
    }

    @Test
    fun removeQuestionFollowerTest(){

        db.addQuestionFollower(user.userId, question1.questionId)

        db.getQuestionFollowers(question1.questionId).thenAccept {
            assertThat(it, equalTo(listOf(user.userId)))
        }.join()

        db.removeQuestionFollower(user.userId, question1.questionId)

        db.getQuestionFollowers(question1.questionId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()
    }

    @Test
    fun addNotificationTest(){
        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()

        db.addNotification(user.userId, sdp.courseId).join()

        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf(user.userId)))
        }.join()

    }

    @Test
    fun getNotificationUserTest(){
        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()

        db.addNotification(user.userId, sdp.courseId).join()
        db.addNotification(nullUser.userId, sdp.courseId).join()

        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf(user.userId, nullUser.userId)))
        }.join()
    }

    @Test
    fun getNotificationTokenTest(){
        val futureToken = CompletableFuture<String>()
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            futureToken.complete(it)
        }
        val token = futureToken.get()
        db.addNotification(user.userId, sdp.courseId).join()
        db.getCourseNotificationTokens(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf(token)))
        }.join()
    }

    @Test
    fun removeNotification(){
        db.addNotification(user.userId, sdp.courseId).join()

        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf(user.userId)))
        }.join()

        db.removeNotification(user.userId, sdp.courseId)

        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()
    }

    @Test
    fun setUserPresenceAddsConnection() {
        db.addUser("0", "test", "testEmail").join()
        db.setUserPresence("0")
        db.getUserById("0").thenAccept {
            assertTrue(it!!.connections.size == 1)
        }
    }

    @Test
    fun removeUserConnectionRemovesAConnection() {
        db.addUser("0", "test", "testEmail").join()
        db.setUserPresence("0")
        db.getUserById("0").thenAccept {
            assertTrue(it!!.connections.size == 1)
            db.removeUserConnection("0")
            assertTrue(it.connections.size == 0)
        }
    }

    @Test
    fun addStatutTest(){
        for(value in UserStatus.values()) {
            db.addStatus(user.userId, sdp.courseId, value)
            db.getUserStatus(user.userId, sdp.courseId).thenAccept {
                assertThat(it, equalTo(value))
            }.join()
        }
    }

    @Test
    fun removeStatutTest(){
        db.addStatus(user.userId, sdp.courseId, UserStatus.STUDENT_ASSISTANT)
        db.getUserStatus(user.userId, sdp.courseId).thenAccept {
            assertThat(it, equalTo(UserStatus.STUDENT_ASSISTANT))
        }.join()
        db.removeStatus(user.userId, sdp.courseId)
        db.getUserStatus(user.userId, sdp.courseId).thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun addAnswerEndorsementTest(){
        db.addAnswerEndorsement(answer1.answerId, user.username)
        db.getAnswerEndorsement(answer1.answerId).thenAccept {
            assertThat(it, equalTo(user.username))
        }.join()
    }

    @Test
    fun getNullEndorsementTest(){
        db.getAnswerEndorsement(answer1.answerId).thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun changeAnswerEndorsementTest(){
        db.addAnswerEndorsement(answer1.answerId, user.username)
        db.getAnswerEndorsement(answer1.answerId).thenAccept {
            assertThat(it, equalTo(user.username))
        }.join()
        db.addAnswerEndorsement(answer1.answerId, nullUser.username)
        db.getAnswerEndorsement(answer1.answerId).thenAccept {
            assertThat(it, equalTo(nullUser.username))
        }.join()
    }

    @Test
    fun removeAnswerEndorsementTest(){
        db.addAnswerEndorsement(answer1.answerId, user.username)
        db.getAnswerEndorsement(answer1.answerId).thenAccept {
            assertThat(it, equalTo(user.username))
        }.join()
        db.removeAnswerEndorsement(answer1.answerId)
        db.getAnswerEndorsement(answer1.answerId).thenAccept {
            assertNull(it)
        }.join()
    }

    @Test
    fun removeChat(){
        val chat =db.addChat("1", "1", "hey")
        db.removeChat(chat.chatId!!)
        db.getChat("1","1").thenAccept {
            assertThat(it.size, equalTo(0))
            }.join()
        }

    @Test
    fun getOtherUsersDoesNotContainTheUser() {
        db.getOtherUsers(user.userId).thenAccept {
            assertThat(it.filter { user2 -> user2.userId != user.userId }.size, equalTo(it.size))
        }.join()
    }

    @Test
    fun getOtherUsersGivesAllOtherUsers() {
        db.getOtherUsers(user.userId).thenAccept { users ->
            db.registeredUsers().thenAccept {
                val usersIds = users.map { user2 -> user2.userId }
                it.forEach { id ->
                    assertThat(usersIds.contains(id), equalTo(true))
                }
            }
        }.join()
    }

    @Test
    fun testInstance(){
        assert(db.getDbInstance()==null)
    }
}