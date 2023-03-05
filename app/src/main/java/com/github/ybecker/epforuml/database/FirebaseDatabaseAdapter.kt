package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.database.Model.*

/**
 * This class represents a database that uses Firebase Realtime Database
 */
class FirebaseDatabaseAdapter : Database() {
    override fun availableCourses(): List<Course> {
        TODO("Not yet implemented")
    }

    override fun getQuestionsForCourse(course: Course): List<Question> {
        TODO("Not yet implemented")
    }

    override fun getAnswersForQuestion(question: Question): List<Answer> {
        TODO("Not yet implemented")
    }

    override fun addQuestion(user: User, course: Course, questionText: String?): Question {
        TODO("Not yet implemented")
    }

    override fun addAnswers(user: User, question: Question, answerText: String?): Answer {
        TODO("Not yet implemented")
    }

    override fun addUser(userId: String, username: String): User {
        TODO("Not yet implemented")
    }

    override fun getUserQuestions(user: User): List<Question> {
        TODO("Not yet implemented")
    }

    override fun getQuestionById(id: String): Question? {
        TODO("Not yet implemented")
    }

    override fun getAnswerById(id: String): Answer? {
        TODO("Not yet implemented")
    }

    override fun getUserById(id: String): User? {
        TODO("Not yet implemented")
    }

    override fun getCourseById(id: String): Course? {
        TODO("Not yet implemented")
    }


}