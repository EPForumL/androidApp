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
import com.github.ybecker.epforuml.database.Model.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import junit.framework.TestCase.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
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
            //database.useEmulator("10.0.2.2", 9000)
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

        question1Future =  db.addQuestion(romain.userId, sdp.courseId, false, "About SDP", "I have question about the SDP course !","https://firebasestorage.googleapis.com/v0/b/epforuml-38150.appspot.com/o/download.jpg?alt=media&token=2549027a-607c-489f-895b-904ab78ebcd9", "")
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
    fun AAAaddMessageRefresh() {
        Firebase.auth.signOut()
        DatabaseManager.user = romain

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
        val futureQuestions = db.getQuestions()
        val questions = futureQuestions.get() // block until the future completes

        // Ensure that all questions have a non-empty title
        for (question in questions) {
            assertNotNull(question.questionTitle)
            assertNotEquals("", question.questionTitle)
        }
    }

    @Test
    fun getCourseQuestionTest(){
        CompletableFuture.allOf(question1Future,question2Future).thenAccept{
                db.getCourseQuestions(sdp.courseId).thenAccept { list ->
                    assertThat(list. map { it.questionId }, equalTo(listOf(question1Future.get().questionId, question2Future.get().questionId)))
                }.join()
            }
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
        }
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
    fun addNewObjectWithTolerateNullArgsTest(){

        question1Future.thenAccept{q1->
            val newAnswer = db.addAnswer(romain.userId, q1.questionId, null)
            db.getAnswerById(newAnswer.answerId).thenAccept {a->
                assertThat(a?.answerText, equalTo(""))
            }.join()

            val newQuestion = db.addQuestion(romain.userId, q1.questionId, false,"title", null, "URI","")

            newQuestion.thenAccept{q->
                db.getQuestionById(q.questionId).thenAccept {
                    assertThat(it?.questionText, equalTo(""))
                }.join()

            }
        }

    }

    @Test
    fun addAndGetNewQuestionEndorsement(){
        question1Future.thenAccept { question1 ->
            db.getQuestionFollowers(question1.questionId).thenAccept {
                assertThat(it, equalTo(emptyList()))
            }.join()
            db.getQuestionFollowers(question1.questionId)
            db.getQuestionFollowers(question1.questionId).thenAccept {
                assertThat(it, equalTo(listOf(romain.userId)))
            }.join()
        }
    }

    @Test
    fun removeQuestionEndorsementTest(){
        question1Future.thenAccept { question1 ->
            db.addQuestionFollower(romain.userId, question1.questionId)
            db.getQuestionFollowers(question1.questionId).thenAccept {
                assertThat(it, equalTo(listOf(romain.userId)))
            }.join()

            db.removeQuestionFollower(romain.userId, question1.questionId)

            db.getQuestionFollowers(question1.questionId).thenAccept {
                assertThat(it, equalTo(listOf()))
            }.join()
        }
    }

    @Test
    fun addAndGetNewAnswerEndorsementTest(){
    answer1Future.thenAccept { answer1 ->
            db.getQuestionFollowers(answer1.answerId).thenAccept {
                assertThat(it, equalTo(emptyList()))
            }.join()
            db.addAnswerEndorsement(romain.userId, answer1.answerId)
            db.getQuestionFollowers(answer1.answerId).thenAccept {
                assertThat(it, equalTo(listOf(romain.userId)))
            }.join()
        }
    }

    @Test
    fun removeAnswerEndorsementTest(){

        answer1Future.thenAccept { answer1 ->
            db.addAnswerEndorsement(romain.userId, answer1.answerId)

            db.getQuestionFollowers(answer1.answerId).thenAccept {
                assertThat(it, equalTo(listOf(romain.userId)))
            }.join()

            db.removeAnswerEndorsement(answer1.answerId)

            db.getQuestionFollowers(answer1.answerId).thenAccept {
                assertThat(it, equalTo(listOf()))
            }.join()
        }
    }

    @Test
    fun addNotificationTest(){
        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()

        db.addNotification(romain.userId, sdp.courseId).join()

        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf(romain.userId)))
        }.join()

    }

    @Test
    fun removeNotificationTest(){

        db.addNotification(romain.userId, sdp.courseId).join()

        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf(romain.userId)))
        }.join()

        db.removeNotification(romain.userId, sdp.courseId)

        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()

    }

    @Test
    fun getNotificationUserTest(){
        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf()))
        }.join()

        db.addNotification(romain.userId, sdp.courseId).join()
        db.addNotification(theo.userId, sdp.courseId).join()

        db.getCourseNotificationUserIds(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf(romain.userId, theo.userId)))
        }.join()
    }

    @Test
    fun getNotificationTokenTest(){
        val futureToken = CompletableFuture<String>()
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            futureToken.complete(it)
        }
        val token = futureToken.get()
        db.addNotification(romain.userId, sdp.courseId).join()
        db.getCourseNotificationTokens(sdp.courseId).thenAccept {
            assertThat(it, equalTo(listOf(token)))
        }.join()
    }


    @Test
    fun setUserPresenceAddsConnection() {
        db.addUser("0", "test", "testEmail").thenAccept { user ->
            db.setUserPresence(user.userId)
            db.getUserById(user.userId).thenAccept {
                assertTrue(it!!.connections.size == 1)
            }
        }
    }

    @Test
    fun removeUserConnectionRemovesAConnection() {
        db.addUser("0", "test", "testEmail").thenAccept { user ->
            db.setUserPresence(user.userId)
            db.getUserById(user.userId).thenAccept {
                assertTrue(it!!.connections.size == 1)
                db.removeUserConnection(it.userId)
                assertTrue(it.connections.size == 0)
            }
        }
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
        db.getOtherUsers(romain.userId).thenAccept { users ->
            db.registeredUsers().thenAccept {
                val usersIds = users.map { user -> user.userId }
                it.forEach { id ->
                    assertThat(usersIds.contains(id), equalTo(true))
                }
            }
        }.join()
    }

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