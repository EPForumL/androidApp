package com.github.ybecker.epforuml

import java.util.concurrent.CompletableFuture

abstract class Database {

    /**
     * This method gives a list a questions for a given course.
     *
     * @param course: the course from which you want to see the questions
     * @return a list of every question asked for the course
     */
    abstract fun getQuestions(course: String?): CompletableFuture<List<String>>?

    /**
     * This method gives a list a answer for a given question.
     *
     * @param course: the course of the question
     * @param question: the question from which you want to see the answers
     * @return a list of all the answers of a given question
     */
    abstract fun getAnswers(course: String?, question: String?): CompletableFuture<List<String>>?

    /**
     * This method post a new question in a given course
     *
     * @param course: the course in which you want to add text
     */
    abstract fun addQuestion(course: String?, question: String?)

    /**
     * This method post a new answer to question in a given course
     *
     * @param course: the course of the question
     * @param question: the question you want to answer
     */
    abstract fun addAnswers(course: String?, question: String?, answer: String?)

    /**
     * This method return the list of every avaiable courses in the current database
     *
     * @return a list of every available courses
     */
    abstract fun availableCourses(): List<String>?
}