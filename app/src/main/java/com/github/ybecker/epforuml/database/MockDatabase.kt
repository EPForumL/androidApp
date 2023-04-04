package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.database.Model.*
import java.util.concurrent.CompletableFuture

/**
 * This class is a database that should only be used for tests
 */
class MockDatabase : Database() {

    private val questions = hashMapOf<String, Question>()
    private val answers = hashMapOf<String, Answer>()
    private val users = hashMapOf<String, User>()
    private val courses = hashMapOf<String, Course>()

    init {
        val course1 = Course("course0","Sweng", mutableListOf(), emptyList())
        courses[course1.courseId] = course1
        val course2 = Course("course1","SDP", mutableListOf(), emptyList())
        courses[course2.courseId] = course2
        val course3 = Course("course2","AnalyseI", mutableListOf(), emptyList())
        courses[course3.courseId] = course3
        val course4 = Course("course3","AnalyseII", mutableListOf(), emptyList())
        courses[course4.courseId] = course4
        val course5 = Course("course4","AnalyseIII", mutableListOf(), emptyList())
        courses[course5.courseId] = course5
        val course6 = Course("course5","AnalyseIV", mutableListOf(), emptyList())
        courses[course6.courseId] = course6
        val course7 = Course("course6","Algo", mutableListOf(), emptyList())
        courses[course7.courseId] = course7
        val course8 = Course("course7","TOC", mutableListOf(), emptyList())
        courses[course8.courseId] = course8
        val course9 = Course("course8","POO", mutableListOf(), emptyList())
        courses[course9.courseId] = course9
        val course10 = Course("course9","POS", mutableListOf(), emptyList())
        courses[course10.courseId] = course10
        val course11 = Course("course10","OS", mutableListOf(), emptyList())
        courses[course11.courseId] = course11
        val course12 = Course("course11","Database", mutableListOf(), emptyList())
        courses[course12.courseId] = course12

        val user1 = User("user1", "TestUser", "", emptyList(), emptyList(), emptyList())
        users[user1.userId] = user1

        val question1 = Question("question1", "course1", "user1", "About ci",
                                "How do I fix the CI ?",
            "https://media.architecturaldigest.com/photos/5890e88033bd1de9129eab0a/4:3/w_960,h_720,c_limit/Artist-Designed%20Album%20Covers%202.jpg",
            mutableListOf())
        questions[question1.questionId] = question1
        val question2 = Question("question2", "course0", "user1", "About Scrum master",
                                "What is a Scrum Master ?", "" , mutableListOf())

        questions[question2.questionId] = question2
        val question3 = Question("question3", "course0", "user1", "Very long question",
            "Extremely long long long long long long long long long long long long long " +
                    "long long long long long long long long long long long long long long long" +
                    "long long long long long long long long long long long long long long long" +
                    "long long long long long long long long long long long long long long long " +
                    "question" ,"", mutableListOf())
        questions[question3.questionId] = question3

        val answer1 = Answer("answer1", "question1", "user1", "première réponse")
        addAnswer(answer1.userId, answer1.questionId, answer1.answerText)

        val answer2 = Answer("answer2", "question1", "user1", "Nan mais je suis pas d'accord")
        addAnswer(answer2.userId, answer2.questionId, answer2.answerText)

        val answer3 = Answer("answer3", "question1", "user1", "Ok alors si tu veux faire ça, " +
                "il faut installer la VM et faire tout depuis chez toi avec le VPN")
        addAnswer(answer3.userId, answer3.questionId, answer3.answerText)

        val answer4 = Answer("answer4", "question1", "user1", "Nan mais je suis pas d'accord")
        addAnswer(answer4.userId, answer4.questionId, answer4.answerText)

        val answer5 = Answer("answer5", "question1", "user1", "Nan mais je suis pas d'accord non plus")
        addAnswer(answer5.userId, answer5.questionId, answer5.answerText)

        this.addSubscription(user1.userId, course1.courseId)
        this.addSubscription(user1.userId, course2.courseId)

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

    override fun addQuestion(userId: String, courseId: String, questionTitle: String, questionText: String?, image_uri: String): Question {
        val questionId = "question${questions.size + 1}"
        val question = Question(questionId, courseId, userId, questionTitle,questionText ?: "", image_uri, emptyList())

        questions[questionId] = question
        courses[courseId]?.questions = courses[courseId]?.questions?.plus(question.questionId) ?: listOf(question.questionId)
        users[userId]?.let {
            val updatedQuestions = it.questions + question.questionId
            users[userId] = it.copy(questions = updatedQuestions)
        }
        return question
    }

    override fun addAnswer(userId: String, questionId: String, answerText: String?): Answer {
        val answerId = "answer${answers.size + 1}"
        val answer = Answer(answerId, questionId, userId, answerText ?: "")
        answers[answerId] = answer
        questions[questionId]?.answers = questions[questionId]?.answers?.plus(answer.answerText) ?: mutableListOf(answer.answerText)

        users[userId]?.let {
            val updatedAnswers = it.answers + answer.answerId
            users[userId] = it.copy(answers = updatedAnswers)
        }
        return answer
    }

    override fun addUser(userId:String, username: String, email: String): CompletableFuture<User> {
        var user = users[userId]
        if(user != null){
            return CompletableFuture.completedFuture(user)
        }
        user = User(userId , username, email)
        users[userId] = user
        return CompletableFuture.completedFuture(user)
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

    override fun addNotification(userId: String, courseId: String) {
        val course = courses[courseId]
        if(course != null) {
            val updatedNotification = course.notifications.filter { it != courseId }
            courses[courseId] = course.copy(notifications = updatedNotification)
        }
    }

    override fun removeNotification(userId: String, courseId: String) {
        val course = courses[courseId]
        if(course != null) {
            val updatedNotification = course.notifications.filter { it != courseId }
            courses[courseId] = course.copy(notifications = updatedNotification)
        }
    }


    override fun availableCourses(): CompletableFuture<List<Course>> {
        return CompletableFuture.completedFuture(courses.values.toList())
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

    override fun getCourseNotifications(courseId: String): CompletableFuture<List<String>> {
        return CompletableFuture.completedFuture(courses[courseId]?.notifications)
    }


}