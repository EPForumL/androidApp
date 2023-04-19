package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.database.Model.*
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

/**
 * An abstract class that defines a set of methods to be implemented by concrete database implementations.
 */
abstract class Database {


    /**
     * Returns a list of all available courses in the current database.
     *
     * @return a list of every available courses
     */
    abstract fun availableCourses(): CompletableFuture<List<Course>>

    /**
     * Retrieves a list of questions for a given course.
     *
     * @param course the course for which to retrieve questions
     * @return a list of all questions for the given course
     */
    abstract fun getCourseQuestions(courseId: String): CompletableFuture<List<Question>>

    /**
     * Retrieves a list of answers for a given question.
     *
     * @param question the question for which to retrieve answers
     * @return a list of all answers for the given question
     */
    abstract fun getQuestionAnswers(questionId: String): CompletableFuture<List<Answer>>


    /**
     * Returns a list of every questions asked by a user.
     *
     * @return a list of every questions asked by a user
     */
    abstract fun getUserQuestions(userId: String): CompletableFuture<List<Question>>

    /**
     * Returns a list of every answers asked by a user.
     *
     * @return a list of every answers asked by a user
     */
    abstract fun getUserAnswers(userId: String): CompletableFuture<List<Answer>>

    /**
     * Returns a list of every courses the user is subscribed to.
     *
     * @return a list of every courses the user is subscribed to
     */
    abstract fun getUserSubscriptions(userId: String): CompletableFuture<List<Course>>

    /**
     * Posts a new course in the forum
     *
     * @param courseName the name of the new course
     * @return the created course
     */
    abstract fun addCourse(courseName: String): Course


    /**
     * Posts a new question in a given course.
     *
     * @param userId the user that adds the question
     * @param courseId the course in which to add the question
     * @param questionTitle the title of the question itself
     * @param questionText the text of the question itself
     * #param image_uri the uri to an image linked with the question
     * @return the question that was posted in database
     */
    abstract fun addQuestion(userId: String, courseId: String, questionTitle: String, questionText: String?,  image_uri: String): Question

    /**
     * Posts a new answer to a question in a given course.
     *
     * @param user the user that adds the answer
     * @param question the question to which to answer
     * @param answerText the text of the answer itself
     * @return the answer that was posted in database
     */
    abstract fun addAnswer(userId: String, questionId: String, answerText: String?): Answer

    /**
     * Adds a user to the database.
     *
     * @param userId the id of the user to add
     * @param username the name of the user to add
     * @param email the email of the user to add
     * @return the user that was added in database
     */
    abstract fun addUser(userId:String, username:String, email:String): CompletableFuture<User>

    /**
     * Adds a user to the database.
     *
     * @param user the user to add
     * @return the user that was added in database
     */
    abstract fun addUser(user: User): CompletableFuture<User>

    /**
     * Removes a user to the database.
     *
     * @param userId the id of the user to remove
     */
    abstract fun removeUser(userId:String)

    /**
     * Updates a user to the database.
     *
     * @param user the locally updated user
     */
    abstract fun updateUser(user: User)

    /**
     * Adds a subscription to the given user for the specified course.
     *
     * @param user the user that want to subscribe
     * @param course the course to which the user is subscribing
     * @return the user with updated subscribe list, or null it the user is not found
     */
    abstract fun addSubscription(userId: String, courseId: String): CompletableFuture<User?>

    /**
     * REmoves a subscription of the given user for a specified course.
     *
     * @param user the user that want to subscribe
     * @param course the course to which the user is subscribing
     */
    abstract fun removeSubscription(userId:String, courseId: String)

    /**
     * Returns the question with the given ID.
     *
     * @param id the ID of the question
     * @return the question with the given ID
     */
    abstract fun getQuestionById(id: String): CompletableFuture<Question?>

    /**
     * Returns the answer with the given ID.
     *
     * @param id the ID of the answer
     * @return the answer with the given ID
     */
    abstract fun getAnswerById(id: String): CompletableFuture<Answer?>

    /**
     * Returns the user with the given ID.
     *
     * @param id the ID of the user
     * @return the user with the given ID
     */

    abstract fun getUserById(id: String): CompletableFuture<User?>

    /**
     * Returns the course with the given ID.
     *
     * @param id the ID of the course
     * @return the course with the given ID
     */
    abstract fun getCourseById(id: String): CompletableFuture<Course?>

    /**
     * Returns all the chats between two users
     *
     * @param userId1 Id of the logged user
     * @param userId2 Id of the external user
     * @returnList of all exchanged messages
     */
    abstract fun getChat(userId1:String, userId2:String): CompletableFuture<List<Chat>>

    /**
     * Posts a new question in a given course.
     *
     * @param senderId the user that sent the chat
     * @param receiverId user that will receive the chat
     * @param text content of the chat
     * @param date time of the chat
     * @return the question that was posted in database
     */
    abstract fun addChat(senderId:String,  receiverId:String,  text: String?) : Chat

    /**
     * Sets user's connected attribute to true and adds a listener to it to detect disconnection.
     */
    abstract fun setUserPresence(userId: String)

    /**
     * Remove a connection from the user's connection list
     */
    abstract fun removeUserConnection(userId: String)

}