package com.github.ybecker.epforuml

import java.util.concurrent.CompletableFuture
import com.github.ybecker.epforuml.Model.*

class FirebaseDatabaseAdapter : Database() {
    override fun getQuestions(course: Course): CompletableFuture<List<Question>>? {
        TODO("Not yet implemented")
    }

    override fun getAnswers(question: Question): CompletableFuture<List<Answer>>? {
        TODO("Not yet implemented")
    }

    override fun addQuestion(user: User, course: Course, questionText: String?) {
        TODO("Not yet implemented")
    }

    override fun addAnswers(user: User, question: Question, answerText: String?) {
        TODO("Not yet implemented")
    }

    override fun availableCourses(): List<Course>? {
        TODO("Not yet implemented")
    }

    override fun getUserQuestions(user: User): List<Question>? {
        TODO("Not yet implemented")
    }


}
