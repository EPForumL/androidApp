package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.database.Model.*
import com.google.firebase.database.*
import java.util.concurrent.CompletableFuture


/**
 * This class represents a database that uses Firebase Realtime Database
 */
class FirebaseDatabaseAdapter(instance: FirebaseDatabase) : Database() {

    // save the database reference
    private val dbInstance = instance
    private val db: DatabaseReference = dbInstance.reference

    // save every useful path to navigate in the database
    private val usersPath = "users"
    private val coursesPath = "courses"
    private val questionsPath = "questions"
    private val answersPath = "answers"
    private val subscriptionsPath = "subscriptions"

    private val courseIdPath = "courseId"
    private val userIdPath = "userId"
    private val questionIdPath = "questionId"
    private val answerIdPath = "answerId"

    private val courseNamePath = "courseName"
    private val usernamePath = "username"

    private val questionTextPath = "questionText"
    private val questionTitlePath = "questionTitle"
    private val answerTextPath = "answerText"

    private val questionURIPath = "imageURI"

    init {
        try {
            dbInstance.useEmulator("10.0.2.2", 9000)
        } catch (e: IllegalStateException) {
            //do nothing if emulator is already enabled
        }
    }

    override fun availableCourses(): CompletableFuture<List<Course>> {
        val future = CompletableFuture<List<Course>>()
        // go in "courses" dir
        db.child(coursesPath).get().addOnSuccessListener {
            val courses = mutableListOf<Course>()
            // add every course that in not null in "courses" in the map
            for (courseSnapshot in it.children) {
                val course = getCourse(courseSnapshot)
                if (course != null) {
                    courses.add(course)
                }
            }
            //complete the future when every children has been added
            future.complete(courses)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    //Note that using course.questions in the main is false because you don't take new values in the db into account !
    override fun getCourseQuestions(courseId: String): CompletableFuture<List<Question>> {
        val future = CompletableFuture<List<Any>>()

        // use private getListOfAny methode with correct arguents
        getListOfAny(listOf(coursesPath,courseId,questionsPath), questionsPath, future) { ds -> getQuestion(ds) }

        //cast the future to the a list of question future
        return future as CompletableFuture<List<Question>>
    }

    //Note that using question.answers in the main is false because you don't take new values in the db into account !
    override fun getQuestionAnswers(questionId: String): CompletableFuture<List<Answer>> {
        val future = CompletableFuture<List<Any>>()

        // use private getListOfAny methode with correct arguents
        getListOfAny(listOf(questionsPath,questionId,answersPath), answersPath, future) { ds -> getAnswer(ds) }

        //cast the future to the a list of answers future
        return future as CompletableFuture<List<Answer>>
    }

    //Note that using user.question in the main is false because you don't take new values in the db into account !
    override fun getUserQuestions(userId: String): CompletableFuture<List<Question>> {
        val future = CompletableFuture<List<Any>>()

        // use private getListOfAny methode with correct arguents
        getListOfAny(listOf(usersPath,userId,questionsPath), questionsPath, future) { ds -> getQuestion(ds) }

        //cast the future to the a list of questions future
        return future as CompletableFuture<List<Question>>
    }

    override fun getUserAnswers(userId: String): CompletableFuture<List<Answer>> {
        val future = CompletableFuture<List<Any>>()

        // use private getListOfAny methode with correct arguents
        getListOfAny(listOf(usersPath,userId,answersPath), answersPath, future) { ds -> getAnswer(ds) }

        //cast the future to the a list of answers future
        return future as CompletableFuture<List<Answer>>
    }


    override fun getUserSubscriptions(userId: String): CompletableFuture<List<Course>> {
        val future = CompletableFuture<List<Any>>()

        // use private getListOfAny methode with correct arguents
        getListOfAny(listOf(usersPath,userId,subscriptionsPath), coursesPath, future) { ds -> getCourse(ds) }

        //cast the future to the a list of courses future
        return future as CompletableFuture<List<Course>>
    }


    override fun addCourse(courseName: String): Course {
        // create a space for the new course in db and save its id
        val newChildRef = db.child(coursesPath).push()
        val courseId = newChildRef.key ?: error("Failed to generate course ID")

        // create the new course using given parameters
        val course = Course(courseId, courseName, emptyList())

        // add the new course in the db
        newChildRef.setValue(course)
        return course
    }


    override fun addQuestion(userId: String, courseId: String, questionTitle: String, questionText: String?, image_uri: String): Question {

        // create a space for the new question in db and save its id
        val newChildRef = db.child(questionsPath).push()
        val questionId = newChildRef.key ?: error("Failed to generate question ID")
        // create the new question using given parameters
        val question = Question(questionId, courseId, userId, questionTitle, questionText ?: "", image_uri, emptyList())

        // add the new question in the db
        newChildRef.setValue(question)

        //add the question in the course's questions list
        db.child(coursesPath).child(courseId).child(questionsPath).child(questionId).setValue(questionId)

        //add the question in the user's questions list
        db.child(usersPath).child(userId).child(questionsPath).child(questionId).setValue(questionId)

        return question
    }

    override fun addAnswer(userId: String, questionId: String, answerText: String?): Answer {
        // create a space for the new answer in sb and save its id
        val newChildRef = db.child(answersPath).push()
        val answerId = newChildRef.key ?: error("Failed to generate answer ID")
        // create the new answer using given parameters
        val answer = Answer(answerId, questionId, userId, answerText ?: "")
        newChildRef.setValue(answer)

        //add the answer in the question's answers list
        db.child(questionsPath).child(questionId).child(answersPath).child(answerId).setValue(answerId)

        //add the answer in the user's questions list
        db.child(usersPath).child(userId).child(answersPath).child(answerId).setValue(answerId)

        return answer
    }

    override fun addUser(userId:String, username: String): CompletableFuture<User> {

        val future = CompletableFuture<User>()
        //try to get the user with given id
        getUserById(userId).thenAccept {
            if(it != null){
                //if user exists complete with it
                future.complete(it)
            }
            else{
                // it user do not exist create a new one and complete with it
                val newUser = User(userId, username, emptyList(), emptyList(), emptyList())
                db.child(usersPath).child(userId).setValue(newUser)
                future.complete(newUser)
            }
        }
        return future
    }

    override fun addSubscription(userId: String, courseId: String): CompletableFuture<User?> {

        db.child(usersPath).child(userId).child(subscriptionsPath).child(courseId).setValue(courseId)

        return getUserById(userId)
    }

    override fun removeUser(userId: String) {
        db.child(usersPath).child(userId).removeValue()
    }

    override fun removeSubscription(userId: String, courseId: String) {
        db.child(usersPath).child(userId).child(subscriptionsPath).child(courseId).removeValue()
    }

    override fun getQuestionById(id: String): CompletableFuture<Question?> {
        val future = CompletableFuture<Any?>()

        //call the private getAnyById with argument for questions
        getAnyById(id, questionsPath, future) {ds -> getQuestion(ds)}

        //cast the general future to a question one
        return future as CompletableFuture<Question?>
    }

    override fun getAnswerById(id: String): CompletableFuture<Answer?> {

        val future = CompletableFuture<Any?>()

        //call the private getAnyById with argument for answers
        getAnyById(id, answersPath, future) {ds -> getAnswer(ds)}

        //cast the general future to a answer one
        return future as CompletableFuture<Answer?>
    }

    override fun getUserById(id: String): CompletableFuture<User?> {
        val future = CompletableFuture<Any?>()

        //call the private getAnyById with argument for users
        getAnyById(id, usersPath, future) {ds -> getUser(ds)}

        //cast the general future to a user one
        return future as CompletableFuture<User?>
    }

    override fun getCourseById(id: String): CompletableFuture<Course?> {
        val future = CompletableFuture<Any?>()

        //call the private getAnyById with argument for courses
        getAnyById(id, coursesPath, future) { ds -> getCourse(ds) }

        //cast the general future to a course one
        return future as CompletableFuture<Course?>
    }

    private fun getAnyById(id: String, dataPath: String, future: CompletableFuture<Any?>, getter: (DataSnapshot) -> Any?) {
        if(id == null){
            future.complete(null)
        }
        // go in the given path
        db.child(dataPath).child(id).get().addOnSuccessListener {
            // use the getter methode to get the researched object and complete given future with it
            future.complete(getter(it))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
    }

    private fun getListOfAny(listPath: List<String>, objectPath: String, future: CompletableFuture<List<Any>>, getter: (DataSnapshot) -> Any?){

        val path = listPath.joinToString(separator = "/")
        db.child(path).get().addOnSuccessListener {
            val modelFutures = mutableListOf<CompletableFuture<Any>>()
            // add every user's subscribed course that is not null in the map
            for (subscriptionSnapshot in it.children) {

                val modelId = subscriptionSnapshot.value as String
                val modelFuture = CompletableFuture<Any>()
                modelFutures.add(modelFuture)

                db.child(objectPath).child(modelId).get().addOnSuccessListener{
                    val model = getter(it)
                    if (model != null) {
                        modelFuture.complete(model)
                    } else {
                        modelFuture.completeExceptionally(Exception("Course not found"))
                    }
                }.addOnFailureListener{
                    future.completeExceptionally(it)
                }

            }
            val models = mutableListOf<Any>()
            CompletableFuture.allOf(*modelFutures.toTypedArray()).thenAccept {
                for (modelFuture in modelFutures) {
                    if (!modelFuture.isCompletedExceptionally) {
                        val model = modelFuture.get()
                        models.add(model)
                    }
                }
                future.complete(models)
            }
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
    }

    private fun getUser(dataSnapshot: DataSnapshot): User? {
        if(dataSnapshot == null){
            return null
        }

        // Get user id
        val userId = dataSnapshot.child(userIdPath).getValue(String::class.java) ?: return null

        // Get username
        val username = dataSnapshot.child(usernamePath).getValue(String::class.java) ?: return null


        // save every answers in a List using getAnswers private methode
        val answers = arrayListOf<String>()
        dataSnapshot.child(answersPath).children.forEach { answerSnapshot ->
            answerSnapshot.key?.let { answers.add(it) }
        }
        // save every question in a List using getQuestion private method
        val questions = arrayListOf<String>()
        dataSnapshot.child(questionsPath).children.forEach { questionSnapshot ->
            questionSnapshot.key?.let { questions.add(it) }
        }
        // save every subscriptions in a List using getCourse private method
        val subscriptions = arrayListOf<String>()
        dataSnapshot.child(subscriptionsPath).children.forEach {subscriptionSnapshot ->
            subscriptionSnapshot.key?.let { subscriptions.add(it) }
        }

        return User(userId, username, questions, answers, subscriptions)
    }

    private fun getQuestion(dataSnapshot: DataSnapshot): Question? {
        if(dataSnapshot == null){
            return null
        }

        val questionId = dataSnapshot.child(questionIdPath).getValue(String::class.java) ?: return null

        val courseId = dataSnapshot.child(courseIdPath).getValue(String::class.java) ?: return null

        val userId = dataSnapshot.child(userIdPath).getValue(String::class.java) ?: return null

        val questionTitle = dataSnapshot.child(questionTitlePath).getValue(String::class.java) ?: return null

        val questionText = dataSnapshot.child(questionTextPath).getValue(String::class.java) ?: return null

        val questionURI = dataSnapshot.child(questionURIPath).getValue(String::class.java) ?: return null

        // save every answers in a List using getAnswers private method
        val answers = arrayListOf<String>()
        dataSnapshot.child(answersPath).children.forEach { answerSnapshot ->
            answerSnapshot.key?.let { answers.add(it) }
        }

        return Question(questionId, courseId, userId, questionTitle, questionText, questionURI, answers)
    }

    private fun getAnswer(dataSnapshot: DataSnapshot): Answer?{
        if(dataSnapshot == null){
            return null
        }
        // save every variables of the answer in a map
        val answerId = dataSnapshot.child(answerIdPath).getValue(String::class.java) ?: return null

        val questionId = dataSnapshot.child(questionIdPath).getValue(String::class.java) ?: return null

        val userId = dataSnapshot.child(userIdPath).getValue(String::class.java) ?: return null

        val answerText = dataSnapshot.child(answerTextPath).getValue(String::class.java) ?: return null

        return Answer(answerId, questionId, userId, answerText)
    }

    private fun getCourse(dataSnapshot: DataSnapshot): Course?{
        if(dataSnapshot == null){
            return null
        }
        // save every non list variables of the course in a map
        val courseId = dataSnapshot.child(courseIdPath).getValue(String::class.java) ?: return null

        val courseName = dataSnapshot.child(courseNamePath).getValue(String::class.java) ?: return null

        // save every questions in a List using getQuestion private method
        val questions = arrayListOf<String>()
        dataSnapshot.child(questionsPath).children.forEach { questionSnapshot ->
            questionSnapshot.key?.let { questions.add(it) }
        }

        return Course(courseId, courseName, questions)
    }

}