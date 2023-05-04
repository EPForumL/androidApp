package com.github.ybecker.epforuml.database

import android.content.ContentValues
import android.util.Log
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.UserStatus
import com.github.ybecker.epforuml.database.Model.*
import com.google.firebase.messaging.FirebaseMessaging
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

/**
 * This class is a database that should only be used for tests
 */
class MockDatabase : Database() {

    private val questions = hashMapOf<String, Question>()
    private val answers = hashMapOf<String, Answer>()
    private val users = hashMapOf<String, User>()
    private val courses = hashMapOf<String, Course>()
    private val chats = hashMapOf<String, Chat>()

    init {
        val course1 = Course("course0","Sweng", mutableListOf(), emptyList())
        courses[course1.courseId] = course1
        val course2 = Course("course1","SDP", mutableListOf(), emptyList())
        courses[course2.courseId] = course2
//        val course3 = Course("course2","AnalyseI", mutableListOf(), emptyList())
//        courses[course3.courseId] = course3
//        val course4 = Course("course3","AnalyseII", mutableListOf(), emptyList())
//        courses[course4.courseId] = course4
//        val course5 = Course("course4","AnalyseIII", mutableListOf(), emptyList())
//        courses[course5.courseId] = course5
//        val course6 = Course("course5","AnalyseIV", mutableListOf(), emptyList())
//        courses[course6.courseId] = course6
//        val course7 = Course("course6","Algo", mutableListOf(), emptyList())
//        courses[course7.courseId] = course7
//        val course8 = Course("course7","TOC", mutableListOf(), emptyList())
//        courses[course8.courseId] = course8
//        val course9 = Course("course8","POO", mutableListOf(), emptyList())
//        courses[course9.courseId] = course9
//        val course10 = Course("course9","POS", mutableListOf(), emptyList())
//        courses[course10.courseId] = course10
//        val course11 = Course("course10","OS", mutableListOf(), emptyList())
//        courses[course11.courseId] = course11
//        val course12 = Course("course11","Database", mutableListOf(), emptyList())
//        courses[course12.courseId] = course12

        val user1 = User("user1", "TestUser", "", emptyList(), emptyList(), emptyList())
        users[user1.userId] = user1
        val userWithCredentials = User("userX", "Jean Dupont", "jdupont@epfl.ch")
        users[userWithCredentials.userId] = userWithCredentials

        val question1 = Question("question1", "course1", "user1", "About ci",
                                "How do I fix the CI ?",
            "",
            mutableListOf(), emptyList())
        questions[question1.questionId] = question1
        val question2 = Question("question2", "course0", "user1", "About Scrum master",
                                "What is a Scrum Master ?", "" , mutableListOf(), emptyList())

        questions[question2.questionId] = question2
        val question3 = Question("question3", "course0", "user1", "Very long question",
            "Extremely long long long long long long long long long long long long long " +
                    "long long long long long long long long long long long long long long long" +
                    "long long long long long long long long long long long long long long long" +
                    "long long long long long long long long long long long long long long long " +
                    "question" ,"", mutableListOf(), emptyList())

        questions[question3.questionId] = question3

        val answer1 = Answer("answer1", "question1", "user1", "première réponse", emptyList(), "")
        addAnswer(answer1.userId, answer1.questionId, answer1.answerText)

        val answer2 = Answer("answer2", "question1", "user1", "Nan mais je suis pas d'accord", emptyList(), "")
        addAnswer(answer2.userId, answer2.questionId, answer2.answerText)

        val answer3 = Answer("answer3", "question1", "user1", "Ok alors si tu veux faire ça, " +
                "il faut installer la VM et faire tout depuis chez toi avec le VPN", emptyList(), ""
        )
        addAnswer(answer3.userId, answer3.questionId, answer3.answerText)

        val answer4 = Answer("answer4", "question1", "user1", "Nan mais je suis pas d'accord", emptyList(), "")
        addAnswer(answer4.userId, answer4.questionId, answer4.answerText)

        val answer5 = Answer("answer5", "question1", "user1", "Nan mais je suis pas d'accord non plus", emptyList(), "")
        addAnswer(answer5.userId, answer5.questionId, answer5.answerText)

        val answer6 = Answer("answer6", "question3", "user1", "Nan mais je suis pas d'accord non plus", emptyList(), "")
        addAnswer(answer6.userId, answer6.questionId, answer6.answerText)

        val chat1 = Chat("chat0",LocalDateTime.now().toString(), user1.userId, user1.userId, "Hey me!")
        addChat(chat1.senderId, chat1.receiverId, chat1.text)
        this.addSubscription(user1.userId, course1.courseId)
        this.addSubscription(user1.userId, course2.courseId)

    }
    override fun getChat(userId1: String, userId2:String): CompletableFuture<List<Chat>> {
        return CompletableFuture.completedFuture(chats.filterValues {
            (it.senderId == userId1 && it.receiverId == userId2)
                ||(it.senderId == userId2 && it.receiverId == userId1)
        }.values.toList().reversed())
    }

    override fun getQuestionFollowers(questionId: String): CompletableFuture<List<String>> {
        return CompletableFuture.completedFuture(questions[questionId]?.followers)
    }

    override fun getAnswerLike(answerId: String): CompletableFuture<List<String>> {
        return CompletableFuture.completedFuture(answers[answerId]?.like)
    }

    override fun addChat(senderId: String, receiverId: String, text: String?): Chat {
        val chatId = "chats${chats.size + 1}"
        val chat = Chat(chatId, LocalDateTime.now().toString(),receiverId,senderId,text)
        chats[chatId] = chat

        return chat
    }

    override fun addChatsWith(senderId: String, receiverId: String): String {

        if(!users[senderId]?.chatsWith?.contains(receiverId)!!){
            users[senderId]?.chatsWith = users[senderId]?.chatsWith?.plus(receiverId) ?: listOf(receiverId)
        }
        if(!users[receiverId]?.chatsWith?.contains(senderId)!!){
            users[receiverId]?.chatsWith = users[receiverId]?.chatsWith?.plus(senderId) ?: listOf(senderId)
        }
        return ""
    }

    override fun getUserId(userName: String): CompletableFuture<String> {
        return CompletableFuture.completedFuture(users.filterValues { it.username== userName}.values.toList().reversed()[0].userId)
    }

    override fun getChatsWith(userID: String): CompletableFuture<List<String>> {
        return CompletableFuture.completedFuture(users[userID]!!.chatsWith)
    }

    override fun getCourseQuestions(courseId: String): CompletableFuture<List<Question>> {
        return CompletableFuture.completedFuture(questions.filterValues { it.courseId == courseId }.values.toList().reversed())
    }

    override fun getQuestionAnswers(questionId: String): CompletableFuture<List<Answer>> {
        return CompletableFuture.completedFuture(answers.filterValues { it.questionId == questionId }.values.toList().reversed())
    }

    override fun addCourse(courseName: String): Course {
        val courseId = "question${questions.size + 1}"

        var course = courses[courseId]
        if(course != null) {
            return course
        }

        course = Course(courseId, courseName, emptyList(), emptyList())
        courses[courseId] = course

        return course
    }

    override fun addQuestion(userId: String, courseId: String, questionTitle: String, questionText: String?, image_uri: String): CompletableFuture<Question> {
        val questionId = "question${questions.size + 1}"
        val question = Question(questionId, courseId, userId, questionTitle,questionText ?: "", image_uri, emptyList(), emptyList())

        questions[questionId] = question
        courses[courseId]?.questions = courses[courseId]?.questions?.plus(question.questionId) ?: listOf(question.questionId)
        users[userId]?.let {
            val updatedQuestions = it.questions + question.questionId
            users[userId] = it.copy(questions = updatedQuestions)
        }
        val question_future = CompletableFuture<Question>()
        question_future.complete(question)
        return question_future
    }

    override fun addAnswer(userId: String, questionId: String, answerText: String?): Answer {
        val answerId = "answer${answers.size + 1}"
        val answer = Answer(answerId, questionId, userId, answerText ?: "", emptyList(), "")
        answers[answerId] = answer
        questions[questionId]?.answers = questions[questionId]?.answers?.plus(answer.answerId) ?: mutableListOf(answer.answerId)

        users[userId]?.let {
            val updatedAnswers = it.answers + answer.answerId
            users[userId] = it.copy(answers = updatedAnswers)
        }
        return answer
    }

    override fun addUser(userId:String, username: String, email: String): CompletableFuture<User> {
        return addUser(User(userId, username, email))
    }

    override fun addUser(user: User): CompletableFuture<User> {
        val dbUser = users[user.userId]
        if(dbUser != null){
            return CompletableFuture.completedFuture(dbUser)
        }
        users[user.userId] = user
        return CompletableFuture.completedFuture(user)
    }

    override fun addQuestionFollower(userId: String, questionId: String) {
        val question = questions[questionId]
        if(question != null) {
            val updatedEndorsement = question.followers + userId
            questions[questionId] = question.copy(followers = updatedEndorsement)
        }
    }

    override fun addAnswerLike(userId: String, answerId: String) {
        val answers = answers[answerId]
        if(answers != null) {
            val updatedEndorsement = answers.like + userId
            this.answers[answerId] = answers.copy(like = updatedEndorsement)
        }
    }

    override fun removeUser(userId: String) {
        users.remove(userId)
    }

    override fun updateUser(user: User) {
        if (user.userId == DatabaseManager.user?.userId) {
            users[user.userId] = user
        }
    }

    override fun addSubscription(userId: String, courseId: String): CompletableFuture<User?> {
        if (users[userId] == null) {
            return CompletableFuture.completedFuture(null)
        } else {
            users[userId]?.let {
                val subscriptions = it.subscriptions.toMutableList()
                if (!subscriptions.contains(courseId)) {
                    subscriptions.add(courseId)
                }
                val updatedSubscription = subscriptions.toList()
                users[userId] = it.copy(subscriptions = updatedSubscription)
            }
            return CompletableFuture.completedFuture(users[userId])
        }
    }

    override fun removeSubscription(userId: String, courseId: String) {
        val user = users[userId]
        if(user != null) {
            val updatedSubscription = user.subscriptions.filter { it != courseId }
            users[userId] = user.copy(subscriptions = updatedSubscription)
        }
    }

    override fun addNotification(userId: String, courseId: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val course = courses[courseId]
        if(course != null) {
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                val updatedNotification = course.notifications + (userId+"/"+it)
                courses[courseId] = course.copy(notifications = updatedNotification)
                future.complete(true)
            }.addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Failed to retrieve notification token for user $userId and course $courseId", e)
                future.complete(false)
            }
        }

        return future
    }

    override fun removeNotification(userId: String, courseId: String) {
        val course = courses[courseId]
        if(course != null) {
            val updatedNotification = course.notifications.filter { it.split("/").get(0) != userId }
            courses[courseId] = course.copy(notifications = updatedNotification)
        }
    }

    override fun removeQuestionFollower(userId: String, questionId: String) {
        val course = questions[questionId]
        if(course != null) {
            val updatedNotification = course.followers.filter { it != userId }
            questions[questionId] = course.copy(followers = updatedNotification)
        }
    }

    override fun removeAnswerLike(userId: String, answerId: String) {
        val answer = answers[answerId]
        if(answer != null) {
            val updatedNotification = answer.like.filter { it != userId }
            answers[answerId] = answer.copy(like = updatedNotification)
        }
    }


    override fun availableCourses(): CompletableFuture<List<Course>> {
        return CompletableFuture.completedFuture(courses.values.toList())
    }

    override fun getAllAnswers(): CompletableFuture<List<Answer>> {
        return CompletableFuture.completedFuture(answers.values.toList())
    }

    override fun registeredUsers(): CompletableFuture<List<String>> {
        return CompletableFuture.completedFuture(users.values.toList().map { u -> u.username })
    }

    override fun getQuestions(): CompletableFuture<List<Question>> {
        return CompletableFuture.completedFuture(questions.values.toList().reversed())
    }

    override fun getQuestionById(id: String): CompletableFuture<Question?> {
        return CompletableFuture.completedFuture(questions.get(id))
    }

    override fun getAnswerById(id: String): CompletableFuture<Answer?> {
        return CompletableFuture.completedFuture(answers.get(id))
    }

    override fun getUserById(id: String): CompletableFuture<User?> {
        return CompletableFuture.completedFuture(users.get(id))
    }

    override fun getCourseById(id: String): CompletableFuture<Course?> {
        return CompletableFuture.completedFuture(courses.get(id))
    }

    override fun getUserQuestions(userId: String): CompletableFuture<List<Question>> {
        return CompletableFuture.completedFuture(questions.filterValues { it.userId == userId }.values.toList().reversed())
    }

    override fun getUserAnswers(userId: String): CompletableFuture<List<Answer>> {
        return CompletableFuture.completedFuture(answers.filterValues { it.userId == userId }.values.toList().reversed())
    }

    override fun getUserSubscriptions(userId: String): CompletableFuture<List<Course>> {
        val list = (users[userId]?.subscriptions ?: listOf()).map { courseId ->
            getCourseById(courseId).thenApply { course ->
                course?.let { it }
            }
        }
        val allFutures = CompletableFuture.allOf(*list.toTypedArray())
        return allFutures.thenApply {
            list.map { it.join() }
        }
    }

    override fun getCourseNotificationTokens(courseId: String): CompletableFuture<List<String>> {
        val list = courses[courseId]?.notifications?.map { it.split("/") }
        if(list == null || list.isEmpty()){
            return CompletableFuture.completedFuture(listOf())
        }
        return CompletableFuture.completedFuture(list.map { it.get(1) })
    }

    override fun getCourseNotificationUserIds(courseId: String): CompletableFuture<List<String>> {
        val list = courses[courseId]?.notifications?.map { it.split("/") }
        if(list == null || list.isEmpty()){
            return CompletableFuture.completedFuture(listOf())
        }
        return CompletableFuture.completedFuture(list.map { it.get(0) })
    }

    override fun setUserPresence(userId: String) {
        users[userId]?.connections?.add(true)
    }

    override fun removeUserConnection(userId: String) {
        val co = users[userId]?.connections
        if (co != null && co.size > 0) {
            co.removeAt(0)
        }
    }

    override fun addStatus(userId: String, courseId: String, statut: UserStatus) {
        val user = users[userId]
        if(user != null){
            val statutString = user.status.filter {it.split("/").get(0) != courseId}
            val updatedUser = user.copy(status = statutString + ("$courseId/${statut.name}"))
            users[userId] = updatedUser
        }
    }

    override fun removeStatus(userId: String, courseId: String) {
        val user = users[userId]
        if(user != null){
            val updatedUser = user.copy(status = user.status.filter { it.split("/").get(0) != courseId})
            users[userId] = updatedUser
        }
    }

    override fun getUserStatus(userId: String, courseId: String): CompletableFuture<UserStatus?> {
        val future = CompletableFuture<UserStatus?>()
        val user = users[userId]
        if(user != null){
            val statusString = user.status.filter {it.split("/")[0] == courseId}

            if(statusString.isEmpty()) return CompletableFuture.completedFuture(null)

             future.complete(UserStatus.valueOf(statusString[0].split("/")[1]))
        } else {
            future.complete(null)
        }

        return future
    }

    override fun getAnswerEndorsement(answerId: String): CompletableFuture<String?> {
        val answer = answers[answerId]
        if(answer!=null && answer.endorsed != ""){
            return CompletableFuture.completedFuture(answer.endorsed)
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun addAnswerEndorsement(answerId: String, username: String) {
        val answer = answers[answerId]
        if(answer!=null){
            val updatedAnswer = answer.copy(endorsed = username)
            answers[answerId] = updatedAnswer
        }
    }

    override fun removeAnswerEndorsement(answerId: String) {
        val answer = answers[answerId]
        if(answer!=null){
            val updatedAnswer = answer.copy(endorsed = "")
            answers[answerId] = updatedAnswer
        }
    }

    override fun removeChat(chatId: String): Boolean {
        return try {
            chats.remove(chatId)
            true
        } catch (e: Exception) {
            false
        }
    }
}