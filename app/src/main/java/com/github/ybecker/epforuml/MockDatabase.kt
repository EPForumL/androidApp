package com.github.ybecker.epforuml

import java.util.concurrent.CompletableFuture

/**
 * This class is a database that should only be used for tests
 */
class MockDatabase : Database() {

    private var db: MutableMap<String, MutableMap<String, MutableList<String>>> = mutableMapOf()
    private val COURSE_LIST: List<String> = mutableListOf("Sweng", "SDP")

    init {
       for (course in COURSE_LIST){
           db.put(course, mutableMapOf())
       }
    }

    override fun getQuestions(course: String?): CompletableFuture<List<String>> {
        val questions = db[course]?.keys?.toList() ?: emptyList()
        return CompletableFuture.completedFuture(questions)
    }

    override fun getAnswers(course: String?, question: String?): CompletableFuture<List<String>>? {
        val answers = db[course]?.get(question) ?: emptyList()
        return CompletableFuture.completedFuture(answers)
    }

    override fun addQuestion(course: String?, question: String?) {
        db.getOrPut(course!!) { mutableMapOf() }.put(question!!, mutableListOf())
    }

    override fun addAnswers(course: String?, question: String?, answer: String?) {
        db.getOrPut(course!!) { mutableMapOf() }.getOrPut(question!!) { mutableListOf() }.add(answer!!)
    }

    override fun availableCourses(): List<String>{
        return COURSE_LIST
    }

}
