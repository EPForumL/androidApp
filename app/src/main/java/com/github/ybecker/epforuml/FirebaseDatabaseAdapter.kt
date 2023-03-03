package com.github.ybecker.epforuml

import java.util.concurrent.CompletableFuture

class FirebaseDatabaseAdapter : Database() {
    override fun getQuestions(course: String?): CompletableFuture<List<String>>? {
        TODO("Not yet implemented")
    }

    override fun getAnswers(course: String?, question: String?): CompletableFuture<List<String>>? {
        TODO("Not yet implemented")
    }

    override fun addQuestion(course: String?, question: String?) {
        TODO("Not yet implemented")
    }

    override fun addAnswers(course: String?, question: String?, answer: String?) {
        TODO("Not yet implemented")
    }

    override fun availableCourses(): List<String>? {
        TODO("Not yet implemented")
    }

}
