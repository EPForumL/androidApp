package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.UserStatus
import com.github.ybecker.epforuml.database.Model.*
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
     * Fetches all existing answers in the database
     *
     * @return a list of every existing answers as a CompletableFuture
     */
    abstract fun getAllAnswers(): CompletableFuture<List<Answer>>

    /**
     * Returns a list of all registered user in the current database.
     *
     * @return a list of every registered user
     */
    abstract fun registeredUsers(): CompletableFuture<List<String>>
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
     * @param user the user from which to get the questions list
     * @return a list of every questions asked by a user
     */
    abstract fun getUserQuestions(userId: String): CompletableFuture<List<Question>>

    /**
     * Returns a list of every answers asked by a user.
     *
     * @param user the user from which to get the answers list
     * @return a list of every answers asked by a user
     */
    abstract fun getUserAnswers(userId: String): CompletableFuture<List<Answer>>

    /**
     * Returns a list of every courses the user is subscribed to.
     *
     * @param user the user from which to get the subscription list
     * @return a list of every courses the user is subscribed to
     */
    abstract fun getUserSubscriptions(userId: String): CompletableFuture<List<Course>>

    /**
     * Returns a list of token for every user that subscribed to notification for the given course.
     *
     * @param course the course from which to get the notification list
     * @return a list of token for every user that subscribed to notification for the given course
     */
    abstract fun getCourseNotificationTokens(courseId: String): CompletableFuture<List<String>>

    /**
     * Returns a list of userId of every user that subscribed to notification for the given course.
     *
     * @param course the course from which to get the notification list
     * @return a list of userId of every user that subscribed to notification for the given course
     */
    abstract fun getCourseNotificationUserIds(courseId: String): CompletableFuture<List<String>>

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
     * @param image_uri the uri to an image linked with the question
     * @return the question that was posted in database
     */
    abstract fun addQuestion(userId: String, courseId: String, questionTitle: String, questionText: String?,  image_uri: String): CompletableFuture<Question>

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
     * Adds an endorsement to a question
     *
     * @param userId the id of the user that endorsed
     * @param questionId the id of the question that is endorsed
     */
    abstract fun addQuestionFollower(userId:String, questionId: String)

    /**
     * Adds an endorsement to a question
     *
     * @param userId the id of the user that endorsed
     * @param answerId the id of the question that is endorsed
     */
    abstract fun addAnswerLike(userId:String, answerId: String)

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
     * Removes a subscription of the given user for a specified course.
     *
     * @param user the user that want to subscribe
     * @param course the course to which the user is subscribing
     */
    abstract fun removeSubscription(userId:String, courseId: String)


    /**
     * Removes an endorsement to a question
     *
     * @param userId the id of the user that removes his endorsement
     * @param answerId the id of the question that lost its endorsement
     */
    abstract fun removeQuestionFollower(userId:String, questionId: String)

    /**
     * Removes an endorsement to a answer
     *
     * @param userId the id of the user that removes his endorsement
     * @param answerId the id of the answer that lost its endorsement
     */
    abstract fun removeAnswerLike(userId:String, answerId: String)

    /**
     * Adds the user in the list of the user to notify.
     *
     * @param user the user that want to have notification
     * @param course the course to which the user want the notifications
     * @return if the fuction worked correctly (it depend on FirebaseMessaging ans it may crash)
     */
    abstract fun addNotification(userId: String, courseId: String): CompletableFuture<Boolean>

    /**
     * remove the user from the list of the user to notify.
     *
     * @param user the user that want to have notification
     * @param course the course to which the user want the notifications
     */
    abstract fun removeNotification(userId:String, courseId: String)

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
     * @return List of all exchanged messages
     */
    abstract fun getChat(userId1:String, userId2:String): CompletableFuture<List<Chat>>

    /**
     * Returns the list of all the userIds that endorsed the given question.
     *
     * @param questionId Id of the question from which we want the endorsements
     * @return List the list of all the userIds that follows the given question
     */
    abstract fun getQuestionFollowers(questionId: String): CompletableFuture<List<String>>

    /**
     * Returns the list of all the userIds that endorsed the given answer.
     *
     * @param answerId Id of the answer from which we want the endorsements
     * @return List the list of all the userIds that like the given answer
     */
    abstract fun getAnswerLike(answerId: String): CompletableFuture<List<String>>


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
     * Posts a new question in a given course.
     *
     * @param senderId the user that sent the chat
     * @param receiverId user that will receive the chat
     * @return a string
     */
    abstract fun addChatsWith(senderId: String, receiverId: String): String

    /**
     * Posts a new question in a given course.
     *
     * @param userName the name of the useer
     * @return the user's ID
     */
    abstract fun getUserId(userName: String): CompletableFuture<String>

    /**
     * Gets list of users the hist chatted with
     *
     * @param userID the id of the useer
     * @return id list of all users he chats with
     */
    abstract fun getChatsWith(userID: String): CompletableFuture<List<String>>

    /**
     * Sets user's connected attribute to true and adds a listener to it to detect disconnection.
     */
    abstract fun setUserPresence(userId: String)

    /**
     * Remove a connection from the user's connection list
     */
    abstract fun removeUserConnection(userId: String)

    /**
    * Removes chat from database
    * @param ChatID to remove
    */
    abstract fun removeChat(chatId:String) : Boolean

    /**
     * Add a Status to a specific user
     *
     * @param userId the id of the you want to add a status to
     * @param courseId the id of the course for where you want to add the status
     * @param status the status you want to add to this user
     */
    abstract fun addStatus(userId: String, courseId: String, status: UserStatus)

    /**
     * Remove a Status to a specific user
     *
     * @param userId the id of the user that remove the status
     * @param courseId the id of the course where you want to remove the status
     */
    abstract fun removeStatus(userId: String, courseId: String)

    /**
     * Add a Status to a specific user
     *
     * @param userId the id of the user that get the status
     * @param courseId the id of the course where you want to get the status
     * @return the status of the user if he has one, null otherwise
     */
    abstract fun getUserStatus(userId: String, courseId: String): CompletableFuture<UserStatus?>

    /**
     * Return the endorser of an answer if there is one, null otherwise
     *
     * @param answerId the id of the answer from which you which the endorser
     * @return the endorser of an answer if there is one, null otherwise
     */
    abstract fun getAnswerEndorsement(answerId: String): CompletableFuture<String?>

    /**
     * Add an endorsement from a user to the answer
     * @param answerId the id of the answer you want to endorse
     * @param username the username of the endorser
     */
    abstract fun addAnswerEndorsement(answerId: String, username: String)

    /**
     * Remove an endorsement from a user to the answer
     * @param answerId the id of the answer you want to remove endorsement
     */
    abstract fun removeAnswerEndorsement(answerId: String)

}