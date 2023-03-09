package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.database.Model.*

/**
 * An abstract class that defines a set of methods to be implemented by concrete database implementations.
 */
abstract class Database {

    /**
     * Returns a list of all available courses in the current database.
     *
     * @return a list of every available courses
     */
    abstract fun availableCourses(): Set<Course>

    /**
     * Retrieves a list of questions for a given course.
     *
     * @param course the course for which to retrieve questions
     * @return a list of all questions for the given course
     */
    abstract fun getCourseQuestions(course: Course): Set<Question>

    /**
     * Retrieves a list of answers for a given question.
     *
     * @param question the question for which to retrieve answers
     * @return a list of all answers for the given question
     */
    abstract fun getQuestionAnswers(question: Question): Set<Answer>


    /**
     * Returns a list of every questions asked by a user.
     *
     * @return a list of every questions asked by a user
     */
    abstract fun getUserQuestions(user: User): Set<Question>

    /**
     * Returns a list of every answers asked by a user.
     *
     * @return a list of every answers asked by a user
     */
    abstract fun getUserAnswers(user: User): Set<Answer>

    /**
     * Posts a new question in a given course.
     *
     * @param user the user that adds the question
     * @param course the course in which to add the question
     * @param questionText the text of the question itself
     * @return the question that was posted in database
     */
    abstract fun addQuestion(user: User, course: Course, questionText: String?): Question

    /**
     * Posts a new answer to a question in a given course.
     *
     * @param user the user that adds the answer
     * @param question the question to which to answer
     * @param answerText the text of the answer itself
     * @return the answer that was posted in database
     */
    abstract fun addAnswer(user: User, question: Question, answerText: String?): Answer

    /**
     * Adds a user to the database.
     *
     * @param userId the id of the user to add
     * @param username the name of the user to add
     * @return the user that was added in database
     */
    abstract fun addUser(userId:String, username:String): User

    /**
     * Returns the question with the given ID.
     *
     * @param id the ID of the question
     * @return the question with the given ID
     */
    abstract fun getQuestionById(id: String): Question?

    /**
     * Returns the answer with the given ID.
     *
     * @param id the ID of the answer
     * @return the answer with the given ID
     */
    abstract fun getAnswerById(id: String): Answer?

    /**
     * Returns the user with the given ID.
     *
     * @param id the ID of the user
     * @return the user with the given ID
     */

    abstract fun getUserById(id: String): User?

    /**
     * Returns the course with the given ID.
     *
     * @param id the ID of the course
     * @return the course with the given ID
     */
    abstract fun getCourseById(id: String): Course?
}