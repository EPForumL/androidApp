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

        // use private getListOfAny methode with correct arguents
        return getListOfAny(listOf(coursesPath,courseId,questionsPath), questionsPath) { ds -> getQuestion(ds) }
                //cast the future to the a list of question future
                as CompletableFuture<List<Question>>
    }

    //Note that using question.answers in the main is false because you don't take new values in the db into account !
    override fun getQuestionAnswers(questionId: String): CompletableFuture<List<Answer>> {

        // use private getListOfAny methode with correct arguents
        return getListOfAny(listOf(questionsPath,questionId,answersPath), answersPath) { ds -> getAnswer(ds) }
                //cast the future to the a list of answers future
                as CompletableFuture<List<Answer>>
    }

    //Note that using user.question in the main is false because you don't take new values in the db into account !
    override fun getUserQuestions(userId: String): CompletableFuture<List<Question>> {

        // use private getListOfAny methode with correct arguents
        return getListOfAny(listOf(usersPath,userId,questionsPath), questionsPath) { ds -> getQuestion(ds) }
                //cast the future to the a list of questions future
                as CompletableFuture<List<Question>>
    }

    //Note that using user.answers in the main is false because you don't take new values in the db into account !
    override fun getUserAnswers(userId: String): CompletableFuture<List<Answer>> {

        // use private getListOfAny methode with correct arguents
        return getListOfAny(listOf(usersPath,userId,answersPath), answersPath) { ds -> getAnswer(ds) }
                //cast the future to the a list of answers future
                as CompletableFuture<List<Answer>>
    }

    //Note that using user.subscriptions in the main is false because you don't take new values in the db into account !
    override fun getUserSubscriptions(userId: String): CompletableFuture<List<Course>> {
        val future = CompletableFuture<List<Any>>()

        // use private getListOfAny methode with correct arguents
        return  getListOfAny(listOf(usersPath,userId,subscriptionsPath), coursesPath) { ds -> getCourse(ds) }
                //cast the future to the a list of courses future
                as CompletableFuture<List<Course>>
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

    override fun getQuestionById(id: String): CompletableFuture<Question?> =
        //call the private getAnyById with argument for questions
        getAnyById(id, questionsPath) {ds -> getQuestion(ds)}
            //cast the general future to a question one
            as CompletableFuture<Question?>



    override fun getAnswerById(id: String): CompletableFuture<Answer?> =

        //call the private getAnyById with argument for answers
         getAnyById(id, answersPath) {ds -> getAnswer(ds)}
                //cast the general future to a answer one
                as CompletableFuture<Answer?>


    override fun getUserById(id: String): CompletableFuture<User?> =

        //call the private getAnyById with argument for users
        getAnyById(id, usersPath) {ds -> getUser(ds)}
                //cast the general future to a user one
                as CompletableFuture<User?>



    override fun getCourseById(id: String): CompletableFuture<Course?> =

        //call the private getAnyById with argument for courses
        getAnyById(id, coursesPath) { ds -> getCourse(ds) }
                //cast the general future to a course one
                as CompletableFuture<Course?>



    private fun getAnyById(id: String, dataPath: String, getter: (DataSnapshot) -> Any?): CompletableFuture<Any?> {
        val future = CompletableFuture<Any?>()
        // go in the given path
        db.child(dataPath).child(id).get().addOnSuccessListener {
            // use the getter methode to get the researched object and complete given future with it
            future.complete(getter(it))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    private fun getListOfAny(listPath: List<String>, objectPath: String, getter: (DataSnapshot) -> Any?): CompletableFuture<List<Any>>{

        val future = CompletableFuture<List<Any>>()
        //creat path by joining the given list
        val path = listPath.joinToString(separator = "/")
        db.child(path).get().addOnSuccessListener {
            val modelFutures = mutableListOf<CompletableFuture<Any?>>()
            // add every user's subscribed course that is not null in the map
            for (subscriptionSnapshot in it.children) {
                //create a future for every child
                val modelId = subscriptionSnapshot.value as String
                //get object for every child
                modelFutures.add(getAnyById(modelId,objectPath, getter))
            }
            convertListOfFutureToFutureOfList(future, modelFutures)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    private fun convertListOfFutureToFutureOfList(future: CompletableFuture<List<Any>>, futureList: List<CompletableFuture<Any?>>){
        val models = mutableListOf<Any>()
        // when every child's future complete, crete the list of models with their result
        CompletableFuture.allOf(*futureList.toTypedArray()).thenAccept {
            for (modelFuture in futureList) {
                if (!modelFuture.isCompletedExceptionally) {
                    val model = modelFuture.get()
                    if(model != null) {
                        models.add(model)
                    }
                }
            }
            //complete the  caller function future
            future.complete(models)
        }
    }

    private fun getUser(dataSnapshot: DataSnapshot): User? {

        // Get user id
        val userId = dataSnapshot.child(userIdPath).getValue(String::class.java)

        // Get username
        val username = dataSnapshot.child(usernamePath).getValue(String::class.java)

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
        if(userId!=null && username!=null){
            return User(userId, username, questions, answers, subscriptions)
        }
        return null
    }

    private fun getQuestion(dataSnapshot: DataSnapshot): Question? {

        val questionId = dataSnapshot.child(questionIdPath).getValue(String::class.java)

        val courseId = dataSnapshot.child(courseIdPath).getValue(String::class.java)

        val userId = dataSnapshot.child(userIdPath).getValue(String::class.java)

        val questionTitle = dataSnapshot.child(questionTitlePath).getValue(String::class.java)

        val questionText = dataSnapshot.child(questionTextPath).getValue(String::class.java)

        val questionURI = dataSnapshot.child(questionURIPath).getValue(String::class.java)

        // save every answers in a List using getAnswers private method
        val answers = arrayListOf<String>()
        dataSnapshot.child(answersPath).children.forEach { answerSnapshot ->
            answerSnapshot.key?.let { answers.add(it) }
        }
        if(questionId!=null && courseId!=null && userId!=null && questionTitle!=null && questionText!=null && questionURI!=null){
            return Question(questionId, courseId, userId, questionTitle, questionText, questionURI, answers)
        }
        return null
    }

    private fun getAnswer(dataSnapshot: DataSnapshot): Answer?{
        // save every variables of the answer in a map
        val answerId = dataSnapshot.child(answerIdPath).getValue(String::class.java)

        val questionId = dataSnapshot.child(questionIdPath).getValue(String::class.java)

        val userId = dataSnapshot.child(userIdPath).getValue(String::class.java)

        val answerText = dataSnapshot.child(answerTextPath).getValue(String::class.java)

        if(answerId!=null && questionId!=null && userId!=null && answerText != null){
            return Answer(answerId, questionId, userId, answerText)
        }
        return null
    }

    private fun getCourse(dataSnapshot: DataSnapshot): Course?{
        // save every non list variables of the course in a map
        val courseId = dataSnapshot.child(courseIdPath).getValue(String::class.java)

        val courseName = dataSnapshot.child(courseNamePath).getValue(String::class.java)

        // save every questions in a List using getQuestion private method
        val questions = arrayListOf<String>()
        dataSnapshot.child(questionsPath).children.forEach { questionSnapshot ->
            questionSnapshot.key?.let { questions.add(it) }
        }
        if(courseId!=null && courseName!=null){
            return Course(courseId, courseName, questions)
        }
        return null
    }

}