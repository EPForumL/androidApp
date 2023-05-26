package com.github.ybecker.epforuml.database

import android.content.Intent
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.UserStatus
import com.github.ybecker.epforuml.database.Model.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class FirebaseDatabaseAdapterTest {


    //you can use this user for tests : email: "test@test.com", password: "tester"
    private lateinit var scenario : ActivityScenario<MainActivity>

    private lateinit var database: FirebaseDatabase
    private lateinit var db: FirebaseDatabaseAdapter
    private lateinit var swEng: Course
    private lateinit var sdp: Course
    private lateinit var romain: User
    private lateinit var theo: User
    private lateinit var question1Future: CompletableFuture<Question>
    private lateinit var question2Future: CompletableFuture<Question>
    private lateinit var questionWithImage: CompletableFuture<Question>
    private lateinit var answer1Future: CompletableFuture<Answer>
    private lateinit var answer2Future: CompletableFuture<Answer>

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

        // local tests works on the emulator but the CI fails
        // so with the try-catch it work but on the real database...
        try{
            database.useEmulator("10.0.2.2", 9000)
        }
        catch (r : IllegalStateException){ }

        db = FirebaseDatabaseAdapter(database)

        val firebaseDB = database.reference

        firebaseDB.child("courses").setValue(null)
        firebaseDB.child("users").setValue(null)
        firebaseDB.child("questions").setValue(null)
        firebaseDB.child("answers").setValue(null)
        firebaseDB.child("chats").setValue(null)

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

        romain = db.addUser("0", "Romain", "testEmail1").get()
        theo = db.addUser("1","Theo", "testEmail2").get()

        questionWithImage = db.addQuestion(romain.userId, sdp.courseId, false, "Image", "Look, this question has an image !","https://firebasestorage.googleapis.com/v0/b/epforuml-38150.appspot.com/o/download.jpg?alt=media&token=2549027a-607c-489f-895b-904ab78ebcd9", "")
        question1Future =  db.addQuestion(romain.userId, sdp.courseId, true, "About SDP", "I have question about the SDP course !","", "")
        question2Future =  db.addQuestion(romain.userId, sdp.courseId, false, "Kotlin", "I think that the lambda with 'it' in Kotlin are great !","","")

        answer2Future = CompletableFuture()
        answer1Future = CompletableFuture()

        question2Future.thenAccept {
            answer1Future.complete(db.addAnswer(romain.userId, it.questionId, "Yes they are !"))
            answer2Future.complete(db.addAnswer(
                romain.userId,
                it.questionId,
                "The exclamation marks are also really great"
            ))
        }

        romain = db.addSubscription(romain.userId, sdp.courseId).get() ?: User("", "error", "")
        romain = db.addSubscription(romain.userId, swEng.courseId).get() ?: User("", "error", "")
        romain = db.addSubscription(romain.userId, swEng.courseId).get() ?: User("", "error", "")

        db.addChatsWith(romain.userId, theo.userId)
        db.addChat(romain.userId, theo.userId ,"Hi Theo this is Romain!")
    }

    @Test
    fun addAMessageRefresh() {
        Thread.sleep(3000) // wait for all futures to complete
        Firebase.auth.signOut()
        DatabaseManager.user = romain
        DatabaseManager.db = db

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java)
        intent.putExtra("externID", theo.userId)


        scenario = ActivityScenario.launch(intent)
        navigateToChat()
        val localDateTime = LocalDateTime.now().toString()
        val chat = db.addChat(theo.userId, romain.userId, localDateTime)
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withText(localDateTime)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
        db.removeChat(chat!!.chatId!!)
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withText(localDateTime)).check(ViewAssertions.doesNotExist())
        scenario.close()

    }

    @Test
    fun addAndGetUser() {
        val user2 = db.addUser("2","TestUser2", "testEmail").get()
        db.getUserById(user2.userId).thenAccept {
            assertThat(it, equalTo(user2))
        }.join()
    }

    @Test
    fun addAndGetChat() {
        val chat2 = db.addChat("1","0", "Hi theo")
        val chat3 = db.addChat("0","1", "Hi theo")
        db.getChat("1","0").thenAccept {
            assertThat(it.size, equalTo(3))
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
        val question = db.addQuestion(romain.userId, sdp.courseId,false, "Question","I have a question.", "https://media.architecturaldigest.com/photos/5890e88033bd1de9129eab0a/4:3/w_960,h_720,c_limit/Artist-Designed%20Album%20Covers%202.jpg","")

        question.thenAccept{
            db.getQuestionById(it.questionId).thenAccept {q->
                assertThat(q, equalTo(question))
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
    fun getAnswerByIdTest(){
        answer1Future.thenAccept { answer1 ->
            db.getAnswerById(answer1.answerId).thenAccept {
                assertThat(it, equalTo(answer1))
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
        db.availableCourses().thenAccept { list ->
            assertTrue(list.map { it.courseId }.containsAll(listOf(swEng.courseId, sdp.courseId)))
        }.join()
    }

    @Test
    fun getQuestionsTest() {

        val q2 = question2Future.get()
        val testQuestion = db.addQuestion(romain.userId, sdp.courseId, true, "NEW QUESTION", "TEXT", "", "").get()

        val questions = db.getQuestions().get()// block until the future completes

        // Ensure that all questions have a non-empty title
        for (question in questions) {
            assertNotNull(question.questionTitle)
            assertNotEquals("", question.questionTitle)
        }
        val questionIdList = questions.map{it.questionId}
        assertTrue(questionIdList.contains(q2.questionId) && questionIdList.contains(testQuestion.questionId))

    }

    @Test
    fun getAllAnswersTest() {
        val answers = db.getAllAnswers().get()

        val ans1 = answer1Future.get()
        val ans2 = answer2Future.get()

        assertThat(answers.map{it.answerId}, equalTo(listOf(ans1.answerId, ans2.answerId)))
    }

    @Test
    fun getChatsWithTest() {
        val chattedWith = db.getChatsWith(romain.userId).get()
        assertThat(chattedWith, equalTo(listOf(theo.userId)))
    }

    @Test
    fun getChatsWithWhenEmptyTest() {
        val newUser = db.addUser("CHATTESTID", "someone", "random.guy@epfl.ch").get()
        val chattedWith = db.getChatsWith(newUser.userId).get()
        assertThat(chattedWith, equalTo(listOf()))
    }

    @Test
    fun getCourseQuestionTest(){
        CompletableFuture.allOf(question1Future,question2Future).thenAccept{
                db.getCourseQuestions(sdp.courseId).thenAccept { list ->
                    assertThat(list. map { it.questionId }, equalTo(listOf(question1Future.get().questionId, question2Future.get().questionId)))
                }.join()
            }.join()
        }



    @Test
    fun getCourseQuestionWhenEmpty(){
        db.getCourseQuestions(swEng.courseId).thenAccept { list ->
            assertThat(list.map { it.questionId }, equalTo(listOf()))
        }.join()
    }

    @Test
    fun getUserQuestionTest(){
        CompletableFuture.allOf(question1Future,question2Future).thenAccept {
            db.getUserQuestions(romain.userId).thenAccept { list ->
                assertThat(
                    list.map { it.questionId }, equalTo(listOf(question1Future.get().questionId, question2Future.get().questionId))
                )
            }.join()
        }
    }

    @Test
    fun getUserQuestionWhenEmpty(){
        db.getUserQuestions(theo.userId).thenAccept { list ->
            assertThat(list.map { it.questionId }, equalTo(listOf()))
        }.join()
    }

    @Test
    fun getUserAnswersTest(){
        db.getUserAnswers(romain.userId).thenAccept {list->
            CompletableFuture.allOf(answer1Future,answer2Future).thenAccept {
                assertThat(
                    list.map { it.answerId },
                    equalTo(listOf(answer1Future.get().answerId, answer2Future.get().answerId))
                )
            }
        }.join()
    }

    @Test
    fun getUserAnswersWhenEmpty(){
        db.getUserAnswers(theo.userId).thenAccept {list->
            assertThat(list.map { it.answerId }, equalTo(listOf()))
        }.join()
    }

    @Test
    fun getQuestionAnswers() {
        question2Future.thenAccept {question2->
            CompletableFuture.allOf( answer1Future, answer2Future).thenAccept {_->
                db.getQuestionAnswers(question2.questionId).thenAccept { list ->
                    assertThat(
                        list.map { it.answerId },
                        equalTo(listOf(answer1Future.get().answerId, answer2Future.get().answerId))
                    )
                }
            }.join()
        }
    }

    @Test
    fun getQuestionAnswersWhenEmpty(){
        question1Future.thenAccept { question1 ->
            db.getQuestionAnswers(question1.questionId).thenAccept { it ->
                assertThat(it.map { it.answerId }, equalTo(listOf()))
            }.join()
        }
    }

    @Test
    fun addExistingUserReturnTheUserTest(){
        val user = db.addUser(romain.userId, "NewRomain", romain.email).get()
        assertThat(user.username, equalTo(romain.username))
    }

    @Test
    fun getUserSubscriptionTest(){
        db.getUserSubscriptions(romain.userId).thenAccept {list->
            assertThat(list.map { it.courseId }, equalTo(listOf(swEng, sdp).map { it.courseId }))
        }.join()

    }

    @Test
    fun getQuestionTitleTest(){
        question1Future.thenAccept { question1 ->
            db.getQuestionById(question1.questionId).thenAccept {
                assertThat(it?.questionTitle, equalTo(question1.questionTitle))
            }.join()
        }.join()
    }

    @Test
    fun addCourse(){
        val newCourseName = "addedCourseTest"
        db.availableCourses().thenAccept { list ->
            assertThat(list.filter{ it.courseId == newCourseName }, equalTo(emptyList()))
        }.join()
        val newCourse = db.addCourse(newCourseName)
        db.availableCourses().thenAccept { list ->
            assertThat(list.filter { it.courseId == newCourse.courseId}, equalTo(listOf(newCourse)))
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
    fun addAndGetChatWith(){
        assertThat( db.getUserById("0").get()?.chatsWith!!.size,equalTo(1))
    }

    @Test
    fun getIdByName(){
        assertThat( db.getUserId("Romain").get(),equalTo("0"))
    }

    @Test
    fun registeredUsers(){
        assertThat(db.registeredUsers().get().size, equalTo(2))
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
    fun addAnswerEndorsementTest(){
        val answer1 = answer1Future.get()

        db.addAnswerEndorsement(answer1.answerId, romain.username)

        val endorsement = db.getAnswerEndorsement(answer1.answerId).get()

        assertThat(endorsement, equalTo(romain.username))
    }

    @Test
    fun removeAnswerEndorsementTest(){

        val answer1 = answer1Future.get()

        addAnswerEndorsementTest()

        db.removeAnswerEndorsement(answer1.answerId)

        val endorsement = db.getAnswerEndorsement(answer1.answerId).get()
        assertNull(endorsement)


    }

    @Test
    fun addNotificationTest(){
        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()

        db.addNotification(romain.userId, sdp.courseId)

        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf(romain.userId)))
        }.join()

        db.getUserNotificationIds(romain.userId).thenAccept {
            assertThat(it, equalTo(listOf(sdp.courseId)))
        }.join()

    }

    @Test
    fun removeNotificationTest(){

        addNotificationTest()

        db.removeNotification(romain.userId, sdp.courseId)

        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()

        db.getCourseNotificationUserIds(romain.userId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()

    }

    @Test
    fun getNotificationUserTest(){
        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()

        db.addNotification(romain.userId, sdp.courseId)
        db.addNotification(theo.userId, sdp.courseId)

        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf(romain.userId, theo.userId)))
        }.join()
    }

//    @Test
//    fun setUserPresenceAddsConnection() {
//
//        db.setUserPresence(romain.userId)
//
//        val newRomain = db.getUserById(romain.userId).get()
//
//        assertTrue(newRomain!!.connections.size == 1)
//    }
//
//    @Test
//    fun removeUserConnectionRemovesAConnection() {
//
//        setUserPresenceAddsConnection()
//
//        db.removeUserConnection(romain.userId)
//
//        val newRomain = db.getUserById(romain.userId).get()
//
//        assertTrue(newRomain?.connections!!.size == 0)
//
//    }

    @Test
    fun removeChat(){
        val chat =db.addChat("1", "1", "hey")
        db.removeChat(chat.chatId!!)
        db.getChat("1","1").thenAccept {
            assertThat(it.size, equalTo(0))
          }.join()
     }

    @Test
    fun addQuestionWithNoURI(){
        db.addQuestion("0","0",false,"URI","????","","").thenAccept{

            assertThat(it.imageURI, `is`(""))
        }
    }

    @Test
    fun addQuestionWithValidURI(){
        db.addQuestion("0","0",false,"URI","????","content://media/external/images/media/1000000157","").thenAccept{

            assertThat(it.imageURI, `is`(""))
        }
    }

    @Test
    fun getOtherUsersDoesNotContainTheUser() {
        db.getOtherUsers(romain.userId).thenAccept {
            assertThat(it.filter { user -> user.userId != romain.userId }.size, equalTo(it.size))
        }.join()
    }

    @Test
    fun getOtherUsersGivesAllOtherUsers() {
        val otherUser = db.getOtherUsers(romain.userId).get()

        db.registeredUsers().thenAccept {
            val usersIds = otherUser.map { user2 -> user2.userId }
            assertTrue(usersIds.containsAll(usersIds))
            assertFalse(usersIds.contains(romain.userId))
        }.join()

    }

    @Test
    fun testInstance(){
        val instance = db.getDbInstance()
        assert(instance==database)
    }

    @Test
    fun addFollowersTest(){
        val question = question2Future.get()
        db.addQuestionFollower(romain.userId, question.questionId)

        db.getQuestionFollowers(question.questionId).thenAccept {
            assertThat(it, equalTo(listOf(romain.userId)))
        }.join()

        db.addQuestionFollower(theo.userId, question.questionId)

        db.getQuestionFollowers(question.questionId).thenAccept {
            assertThat(it, equalTo(listOf(romain.userId, theo.userId)))
        }.join()
    }

    @Test
    fun removeFollowersTest() {

        val question = question2Future.get()
        addFollowersTest()

        db.removeQuestionFollower(romain.userId, question.questionId)

        db.getQuestionFollowers(question.questionId).thenAccept {
            assertThat(it, equalTo(listOf(theo.userId)))
        }.join()

        db.removeQuestionFollower(theo.userId, question.questionId)

        db.getQuestionFollowers(question.questionId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()

    }

    @Test
    fun addAnswerLikeTest() {

        val answer = answer2Future.get()
        db.addAnswerLike(romain.userId, answer.answerId)


        db.getAnswerLike(answer.answerId).thenAccept {
            assertThat(it, equalTo(listOf(romain.userId)))
        }.join()

        db.addAnswerLike(theo.userId, answer.answerId)

        db.getAnswerLike(answer.answerId).thenAccept {
            assertThat(it, equalTo(listOf(romain.userId, theo.userId)))
        }.join()
    }

    @Test
    fun removeAnswerLikeTest() {
        addAnswerLikeTest()

        val answer = answer2Future.get()

        db.removeAnswerLike(romain.userId, answer.answerId)

        db.getAnswerLike(answer.answerId).thenAccept {
            assertThat(it, equalTo(listOf(theo.userId)))
        }.join()

        db.removeAnswerLike(theo.userId, answer.answerId)

        db.getAnswerLike(answer.answerId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()

    }

    @Test
    fun addUserStatusTest() {

        db.addStatus(romain.userId, swEng.courseId, UserStatus.TEACHER)

        db.getUserStatus(romain.userId, swEng.courseId).thenAccept {
            assertThat(it, equalTo(UserStatus.TEACHER))
        }.join()

        db.addStatus(theo.userId, sdp.courseId, UserStatus.STUDENT_ASSISTANT)

        db.getUserStatus(theo.userId, sdp.courseId).thenAccept {
            assertThat(it, equalTo(UserStatus.STUDENT_ASSISTANT))
        }.join()

        db.addStatus(romain.userId, sdp.courseId, UserStatus.ASSISTANT)

        db.getUserStatus(romain.userId, sdp.courseId).thenAccept {
            assertThat(it, equalTo(UserStatus.ASSISTANT))
        }.join()
    }

    @Test
    fun removeUserStatusTest() {
        addUserStatusTest()

        db.removeStatus(romain.userId, swEng.courseId)

        db.getUserStatus(romain.userId, swEng.courseId).thenAccept {
            assertThat(it, equalTo(null))
        }.join()

    }

    @Test
    fun updateUserTest() {

        DatabaseManager.user = romain

        val newUsername = "RANDOM"

        romain.username = newUsername
        val user = db.getUserById(romain.userId).get()
        assertThat(user?.username, not(equalTo(newUsername)))

        db.updateUser(romain)

        val newUser = db.getUserById(romain.userId).get()
        assertThat(newUser?.username, equalTo(newUsername))
    }

    @Test
    fun updateLocalizationTest() {

        val randomLatLng = LatLng(6.0,9.0)

        assertThat(romain.latitude, not(equalTo(randomLatLng.latitude)))
        assertThat(romain.latitude, not(equalTo(randomLatLng.longitude)))

        db.updateLocalization(romain.userId,randomLatLng,true)

        val newRomain = db.getUserById(romain.userId).get()
        assertThat(newRomain?.latitude, equalTo(randomLatLng.latitude))
        assertThat(newRomain?.longitude, equalTo(randomLatLng.longitude))
    }

    //getAnswerEndorsement

    private fun navigateToChat() {
        Espresso.onView(ViewMatchers.withContentDescription(R.string.open))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.nav_chat)).perform(ViewActions.click())
        scenario.onActivity { activity ->
            val view: RecyclerView = activity.findViewById(R.id.recycler_chat_home)
            view.findViewById<CardView>(R.id.buttonChatWith).performClick()
        }
    }
}
