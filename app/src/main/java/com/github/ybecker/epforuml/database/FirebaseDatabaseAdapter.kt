package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.database.Model.*
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.*
import java.util.concurrent.CompletableFuture


/**
 * This class represents a database that uses Firebase Realtime Database
 */
class FirebaseDatabaseAdapter : Database() {

    // save the database reference
    private val dbInstance = FirebaseDatabase.getInstance("https://epforuml-38150-default-rtdb.europe-west1.firebasedatabase.app")
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
            future.complete(courses)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    //Note that using course.questions in the main is false because you don't take new values in the db into account !
    override fun getCourseQuestions(courseId: String): CompletableFuture<List<Question>> {
        val future = CompletableFuture<List<Question>>()
        // go in "courses/courseId/questions" dir
        db.child(coursesPath).child(courseId).child(questionsPath).get().addOnSuccessListener {
            val questionFutures = mutableListOf<CompletableFuture<Question>>()

            // add every course's question that in not null in the map
            for (questionSnapshot in it.children) {

                val questionId = questionSnapshot.value as String
                val questionFuture = CompletableFuture<Question>()
                questionFutures.add(questionFuture)

                db.child(questionsPath).child(questionId).get().addOnSuccessListener{
                    val question = getQuestion(it)
                    if (question != null) {
                        questionFuture.complete(question)
                    } else {
                        questionFuture.completeExceptionally(Exception("Question not found"))
                    }
                }
            }
            val questions = mutableListOf<Question>()
            CompletableFuture.allOf(*questionFutures.toTypedArray()).thenAccept {
                for (questionFuture in questionFutures) {
                    if (!questionFuture.isCompletedExceptionally) {
                        val question = questionFuture.get()
                        questions.add(question)
                    }
                }
                future.complete(questions)
            }
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    //Note that using question.answers in the main is false because you don't take new values in the db into account !
    override fun getQuestionAnswers(questionId: String): CompletableFuture<List<Answer>> {
        val future = CompletableFuture<List<Answer>>()
        // go in "question/questionId" dir
        db.child(questionsPath).child(questionId).child(answersPath).get().addOnSuccessListener {
            val answerFutures = mutableListOf<CompletableFuture<Answer>>()

            // add every question's answer that is not null in the map
            for (answersSnapshot in it.children) {
                val answerId = answersSnapshot.value as String
                val answerFuture = CompletableFuture<Answer>()
                answerFutures.add(answerFuture)

                db.child(answersPath).child(answerId).get().addOnSuccessListener{
                    val answer = getAnswer(it)
                    if (answer != null) {
                        answerFuture.complete(answer)
                    } else {
                        answerFuture.completeExceptionally(Exception("Answer not found"))
                    }
                }.addOnFailureListener{
                    future.completeExceptionally(it)
                }
            }
            val questions = mutableListOf<Answer>()
            CompletableFuture.allOf(*answerFutures.toTypedArray()).thenAccept {
                for (answerFuture in answerFutures) {
                    if (!answerFuture.isCompletedExceptionally) {
                        val question = answerFuture.get()
                        questions.add(question)
                    }
                }
                future.complete(questions)
            }
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    //Note that using user.question in the main is false because you don't take new values in the db into account !
    override fun getUserQuestions(userId: String): CompletableFuture<List<Question>> {
        val future = CompletableFuture<List<Question>>()
        // go in "user/userId" dir
        db.child(usersPath).child(userId).child(questionsPath).get().addOnSuccessListener {
            val questionFutures = mutableListOf<CompletableFuture<Question>>()

            // add every course's question that in not null in the map
            for (questionSnapshot in it.children) {

                val questionId = questionSnapshot.value as String
                val questionFuture = CompletableFuture<Question>()
                questionFutures.add(questionFuture)

                db.child(questionsPath).child(questionId).get().addOnSuccessListener{
                    val question = getQuestion(it)
                    if (question != null) {
                        questionFuture.complete(question)
                    } else {
                        questionFuture.completeExceptionally(Exception("Question not found"))
                    }
                }
            }
            val questions = mutableListOf<Question>()
            CompletableFuture.allOf(*questionFutures.toTypedArray()).thenAccept {
                for (questionFuture in questionFutures) {
                    if (!questionFuture.isCompletedExceptionally) {
                        val question = questionFuture.get()
                        questions.add(question)
                    }
                }
                future.complete(questions)
            }
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    override fun getUserAnswers(userId: String): CompletableFuture<List<Answer>> {
        val future = CompletableFuture<List<Answer>>()
        // go in "user/userId" dir
        db.child(usersPath).child(userId).child(answersPath).get().addOnSuccessListener {
            val answerFutures = mutableListOf<CompletableFuture<Answer>>()

            // add every question's answer that is not null in the map
            for (answersSnapshot in it.children) {
                val answerId = answersSnapshot.value as String
                val answerFuture = CompletableFuture<Answer>()
                answerFutures.add(answerFuture)

                db.child(answersPath).child(answerId).get().addOnSuccessListener{
                    val answer = getAnswer(it)
                    if (answer != null) {
                        answerFuture.complete(answer)
                    } else {
                        answerFuture.completeExceptionally(Exception("Answer not found"))
                    }
                }.addOnFailureListener{
                    future.completeExceptionally(it)
                }
            }
            val questions = mutableListOf<Answer>()
            CompletableFuture.allOf(*answerFutures.toTypedArray()).thenAccept {
                for (answerFuture in answerFutures) {
                    if (!answerFuture.isCompletedExceptionally) {
                        val question = answerFuture.get()
                        questions.add(question)
                    }
                }
                future.complete(questions)
            }
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    override fun getUserSubscriptions(userId: String): CompletableFuture<List<Course>> {
        val future = CompletableFuture<List<Course>>()
        // go in "user/subscriptions" dir
        db.child(usersPath).child(userId).child(subscriptionsPath).get().addOnSuccessListener {
            val courseFutures = mutableListOf<CompletableFuture<Course>>()
            // add every user's subscribed course that is not null in the map
            for (subscriptionSnapshot in it.children) {

                val courseId = subscriptionSnapshot.value as String
                val courseFuture = CompletableFuture<Course>()
                courseFutures.add(courseFuture)

                db.child(coursesPath).child(courseId).get().addOnSuccessListener{
                    val course = getCourse(it)
                    if (course != null) {
                        courseFuture.complete(course)
                    } else {
                        courseFuture.completeExceptionally(Exception("Course not found"))
                    }
                }.addOnFailureListener{
                    future.completeExceptionally(it)
                }

            }
            val courses = mutableListOf<Course>()
            CompletableFuture.allOf(*courseFutures.toTypedArray()).thenAccept {
                for (courseFuture in courseFutures) {
                    if (!courseFuture.isCompletedExceptionally) {
                        val course = courseFuture.get()
                        courses.add(course)
                    }
                }
                future.complete(courses)
            }
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }


    override fun addQuestion(userId: String, courseId: String, questionTitle: String, questionText: String?, image_uri: String): Question {

        // create a space for the new question in sb and save its id
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
        getUserById(userId).thenAccept {
            if(it != null){
                future.complete(it)
            }
            else{
                val newUser = User(userId, username, emptyList(), emptyList(), emptyList())
                db.child(usersPath).child(userId).setValue(newUser)
                future.complete(newUser)
            }
        }
        return future
    }

    override fun addSubscription(userId: String, courseId: String): CompletableFuture<User?> {
//        val subscriptionsId = getUserSubscriptions(userId).map { it.courseId }.toMutableList()
//        if (subscriptionsId.contains(courseId)) {
//            return getUserById(userId)
//        }
//        subscriptionsId.add(courseId)
//        val updatedSubscriptionsId = subscriptionsId.toList()
        db.child(usersPath).child(userId).child(subscriptionsPath).child(courseId).setValue(courseId)

        return getUserById(userId)
    }

    override fun getQuestionById(questionId: String): CompletableFuture<Question?> {
        val future = CompletableFuture<Question?>()
        db.child(questionsPath).child(questionId).get()
            .addOnSuccessListener {
                future.complete(getQuestion(it))
            }.addOnFailureListener {
                future.completeExceptionally(it)
            }
        return future
    }

    override fun getAnswerById(id: String): CompletableFuture<Answer?> {
        if(id == null){
            return CompletableFuture.completedFuture(Answer())
        }
        val future = CompletableFuture<Answer?>()
        // go in "answers/id" and use private methode to get the answer
        db.child(answersPath).child(id).get().addOnSuccessListener {
            future.complete(getAnswer(it))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    override fun getUserById(id: String): CompletableFuture<User?> {
        if(id == null){
            return CompletableFuture.completedFuture(User())
        }
        val future = CompletableFuture<User?>()
        // go in "users/id" and use private methode to get the user
        db.child(usersPath).child(id).get().addOnSuccessListener {
            future.complete(getUser(it))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    override fun getCourseById(id: String): CompletableFuture<Course?> {
        if(id == null){
            return CompletableFuture.completedFuture(Course())
        }
        val future = CompletableFuture<Course?>()
        // go in "courses/id" and use private methode to get the course
        db.child(coursesPath).child(id).get().addOnSuccessListener {
            future.complete(getCourse(it))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
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