package com.github.ybecker.epforuml

import java.util.concurrent.CompletableFuture
import com.github.ybecker.epforuml.Model.*

abstract class Database {

    /**
     * This method gives a list a questions for a given course.
     *
     * @param course: the course from which you want to see the questions
     * @return a list of every question asked for the course
     */
    abstract fun getQuestions(course: Course): CompletableFuture<List<Question>>?

    /**
     * This method gives a list a answer for a given question.
     *
     * @param course: the course of the question
     * @param question: the question from which you want to see the answers
     * @return a list of all the answers of a given question
     */
    abstract fun getAnswers(question: Question): CompletableFuture<List<Answer>>?

    /**
     * This method post a new question in a given course
     *
     * @param course: the course in which you want to add text
     */
    abstract fun addQuestion(user: User, course: Course, questionText: String?)

    /**
     * This method post a new answer to question in a given course
     *
     * @param course: the course of the question
     * @param question: the question you want to answer
     */
    abstract fun addAnswers(user: User, question: Question, answerText: String?)

    /**
     * This method return the list of every avaiable courses in the current database
     *
     * @return a list of every available courses
     */
    abstract fun availableCourses(): List<Course>?

    /**
     * This method return every Questions asked by a User
     *
     * @return a list of every Questions asked by a User
     */
    abstract fun getUserQuestions(user: User): List<Question>?
}