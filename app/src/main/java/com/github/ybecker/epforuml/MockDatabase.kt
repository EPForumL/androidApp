package com.github.ybecker.epforuml

import java.util.concurrent.CompletableFuture
import com.github.ybecker.epforuml.Model.*

/**
 * This class is a database that should only be used for tests
 */
class MockDatabase : Database() {

    private val questions = hashMapOf<String, Question>()
    private val answers = hashMapOf<String, Answer>()
    private val users = hashMapOf<String, User>()
    private val courses = hashMapOf<String, Course>()

    init {

        val course1 = Course("0","Sweng", mutableListOf())
        courses[course1.courseId] = course1
        val course2 = Course("1","SDP", mutableListOf())
        courses[course2.courseId] = course2
        val user1 = User("user1", "TestUser", emptyList())
        users[user1.userId] = user1
    }

    override fun getQuestions(course: Course): CompletableFuture<List<Question>>? {
        val future = CompletableFuture<List<Question>>()
        val courseQuestions = questions.filterValues { it.courseId == course.courseId }.values.toList()
        future.complete(courseQuestions)
        return future
    }

    override fun getAnswers(question: Question): CompletableFuture<List<Answer>>? {
        val future = CompletableFuture<List<Answer>>()
        val questionAnswers = answers.filterValues { it.questionId == question.questionId }.values.toList()
        future.complete(questionAnswers)
        return future
    }

    override fun addQuestion(user: User, course: Course, questionText: String?) {
        val questionId = "question${questions.size + 1}"
        val question = Question(questionId, course.courseId, user.userId, questionText ?: "", emptyList())
        questions[questionId] = question
        courses[course.courseId]?.answers = courses[course.courseId]?.answers?.plus(question) ?: listOf(question)
    }

    override fun addAnswers(user: User, question: Question, answerText: String?) {
        val answerId = "answer${answers.size + 1}"
        val answer = Answer(answerId, question.questionId, user.userId, answerText ?: "")
        answers[answerId] = answer
        questions[question.questionId]?.answers = questions[question.questionId]?.answers?.plus(answer) ?: listOf(answer)
    }

    override fun availableCourses(): List<Course>? {
        return courses.values.toList()
    }

    override fun getUserQuestions(user: User): List<Question>? {
        val userQuestions = questions.filterValues { it.userId == user.userId }.values.toList()
        return userQuestions.toList()
    }

}