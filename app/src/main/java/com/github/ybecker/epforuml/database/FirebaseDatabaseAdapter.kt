package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.database.Model.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * This class represents a database that uses Firebase Realtime Database
 */
class FirebaseDatabaseAdapter : Database() {

    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun availableCourses(): Set<Course> {
        TODO("Not yet implemented")
    }

    override fun getQuestionsForCourse(course: Course): Set<Question> {
        TODO("Not yet implemented")
    }

    override fun getAnswersForQuestion(question: Question): Set<Answer> {
        TODO("Not yet implemented")
    }

    override fun addQuestion(user: User, course: Course, questionText: String?): Question {
        TODO("Not yet implemented")
    }

    override fun addAnswer(user: User, question: Question, answerText: String?): Answer {
        TODO("Not yet implemented")
    }

    override fun addUser(userId: String, username: String): User {
        TODO("Not yet implemented")
    }

    override fun getUserQuestions(user: User): Set<Question> {
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