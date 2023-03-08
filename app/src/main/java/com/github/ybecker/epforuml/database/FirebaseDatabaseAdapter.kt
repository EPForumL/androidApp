package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.database.Model.*
import com.google.firebase.database.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

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

    private val courseIdPath = "courseId"
    private val userIdPath = "userId"
    private val questionIdPath = "questionId"
    private val answerIdPath = "answerId"

    private val courseNamePath = "courseName"
    private val usernamePath = "username"

    private val questionTextPath = "questionText"
    private val answerTextPath = "answerText"

    override fun availableCourses(): Set<Course> {
        val future = CompletableFuture<Set<Course>>()
        // go in "courses" dir
        db.child(coursesPath).get().addOnSuccessListener {
            val courses = mutableSetOf<Course>()
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

        return future.get(5,TimeUnit.SECONDS)
    }

    //Note that using course.questions in the main is false because you don't take new values in the db into account !
    override fun getCourseQuestions(course: Course): Set<Question> {
        // go in "courses/courseId/questions" dir
        val future = CompletableFuture<Set<Question>>()
        db.child(coursesPath).child(course.courseId).child(questionsPath).get()
            .addOnSuccessListener {
                val questions = mutableSetOf<Question>()
                // add every course's question that in not null in the map
                for (courseSnapshot in it.children) {
                    val question = getQuestion(courseSnapshot)
                    if (question != null) {
                        questions.add(question)
                    }
                }
                future.complete(questions)
            }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future.get(5, TimeUnit.SECONDS)
    }

    //Note that using question.answers in the main is false because you don't take new values in the db into account !
    override fun getQuestionAnswers(question: Question): Set<Answer> {
        val future = CompletableFuture<Set<Answer>>()
        // go in "question/questionId" dir
        db.child(questionsPath).child(question.questionId).child(answersPath).get().addOnSuccessListener {
            val answers = mutableSetOf<Answer>()
            // add every question's answer that is not null in the map
            for (courseSnapshot in it.children) {
                val answer = getAnswer(courseSnapshot)
                if (answer != null) {
                    answers.add(answer)
                }
            }
            future.complete(answers)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future.get(5,TimeUnit.SECONDS)
    }

    //Note that using user.question in the main is false because you don't take new values in the db into account !
    override fun getUserQuestions(user: User): Set<Question> {
        val future = CompletableFuture<Set<Question>>()
        // go in "user/userId" dir
        db.child(usersPath).child(user.userId).child(questionsPath).get().addOnSuccessListener {
            val questions = mutableSetOf<Question>()
            // add every user's question that is not null in the map
            for (courseSnapshot in it.children) {
                val question = getQuestion(courseSnapshot)
                if (question != null) {
                    questions.add(question)
                }
            }
            future.complete(questions)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future.get(5,TimeUnit.SECONDS)
    }

    override fun getUserAnswers(user: User): Set<Answer> {
        val future = CompletableFuture<Set<Answer>>()
        // go in "user/userId" dir
        db.child(usersPath).child(user.userId).child(answersPath).get().addOnSuccessListener {
            val answers = mutableSetOf<Answer>()
            // add every user's question that is not null in the map
            for (courseSnapshot in it.children) {
                val answer = getAnswer(courseSnapshot)
                if (answer != null) {
                    answers.add(answer)
                }
            }
            future.complete(answers)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future.get(5,TimeUnit.SECONDS)
    }

    override fun addQuestion(user: User, course: Course, questionText: String?): Question {
        // create a space for the new question in sb and save its id
        val newChildRef = db.child(questionsPath).push()
        val questionId = newChildRef.key ?: error("Failed to generate question ID")
        // create the new question using given parameters
        val question = Question(questionId, course.courseId, user.userId, questionText ?: "", emptyList())
        // add the new question in the db
        newChildRef.setValue(question)

        //add the question in the course's questions list
        db.child(coursesPath).child(course.courseId).child(questionsPath).child(questionId).setValue(question)
        getCourseQuestions(course)

        //add the question in the user's questions list
        db.child(usersPath).child(user.userId).child(questionsPath).child(questionId).setValue(question)

        return question
    }

    override fun addAnswer(user: User, question: Question, answerText: String?): Answer {
        // create a space for the new answer in sb and save its id
        val newChildRef = db.child(answersPath).push()
        val answerId = newChildRef.key ?: error("Failed to generate answer ID")
        // create the new answer using given parameters
        val answer = Answer(answerId, question.questionId, user.userId, answerText ?: "")
        newChildRef.setValue(answer)

        //TODO for a next sprint modify List of object by List of id it will be clearer and takes less memory in DB !

        //add the answer in the question's answers list
        db.child(questionsPath).child(question.questionId).child(answersPath).child(answerId).setValue(answer)

        //add the answer in the user's questions list
        db.child(usersPath).child(user.userId).child(answersPath).child(answerId).setValue(answer)

        //add the answer in the course's question's answers list
        db.child(coursesPath).child(question.courseId).child(questionsPath).child(question.questionId).child(answersPath).child(answerId).setValue(answer)

        //add the answer in the user's question's answers list
        db.child(usersPath).child(user.userId).child(questionsPath).child(question.questionId).child(answersPath).child(answerId).setValue(answer)


        return answer
    }

    override fun addUser(userId:String, username: String): User {

        val getUser = getUserById(userId)
        if(getUser != null){
            return getUser
        }

        // create a space for the new question in sb and save its id
        val newUser = User(userId, username, emptyList(), emptyList())
        db.child(usersPath).child(userId).setValue(newUser)
        return newUser
    }

    override fun getQuestionById(id: String): Question? {
        val future = CompletableFuture<Question?>()
        // go in "questions/id" and use private methode to get the question
        db.child(questionsPath).child(id).get().addOnSuccessListener {
            future.complete(getQuestion(it))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future.get(5,TimeUnit.SECONDS)
    }

    override fun getAnswerById(id: String): Answer? {
        val future = CompletableFuture<Answer?>()
        // go in "answers/id" and use private methode to get the answer
        db.child(answersPath).child(id).get().addOnSuccessListener {
            future.complete(getAnswer(it))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future.get(5,TimeUnit.SECONDS)
    }

    override fun getUserById(id: String): User? {
        val future = CompletableFuture<User?>()
        // go in "users/id" and use private methode to get the user
        db.child(usersPath).child(id).get().addOnSuccessListener {
            future.complete(getUser(it))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future.get(5,TimeUnit.SECONDS)
    }

    override fun getCourseById(id: String): Course? {
        val future = CompletableFuture<Course?>()
        // go in "courses/id" and use private methode to get the course
        db.child(coursesPath).child(id).get().addOnSuccessListener {
            future.complete(getCourse(it))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future.get(5,TimeUnit.SECONDS)
    }

    private fun getUser(dataSnapshot: DataSnapshot): User? {
        if(dataSnapshot.value == null){
            return null
        }

        // save every non list variables of the user in a map
        val userMap = hashMapOf<String, Any?>()
        dataSnapshot.children.forEach {
            if (it.key != questionsPath && it.key!=answersPath) {
                userMap[it.key!!] = it.value
            }
        }
        // save every answers in a List using getAnswers private methode
        val answers = arrayListOf<Answer>()
        dataSnapshot.child(answersPath).children.forEach {
            val answer = getAnswer(it)
            if(answer != null){
                answers.add(answer)
            }
        }
        // save every question in a List using getQuestion private method
        val questions = arrayListOf<Question>()
        dataSnapshot.child(questionsPath).children.forEach {
            val question = getQuestion(it)
            if(question != null){
                questions.add(question)
            }
        }

        return User(
            userMap[userIdPath] as String,
            userMap[usernamePath] as String,
            questions,
            answers
        )
    }

    private fun getQuestion(dataSnapshot: DataSnapshot): Question? {
        if(dataSnapshot.value == null){
            return null
        }
        // save every non list variables of the question in a map
        var questionMap = hashMapOf<String, Any?>()
        dataSnapshot.children.forEach {
            if (it.key != answersPath) {
                questionMap[it.key!!] = it.value
            }
        }
        // save every answers in a List using getAnswers private method
        val answers = arrayListOf<Answer>()
        dataSnapshot.child(answersPath).children.forEach {
            val answer = getAnswer(it)
            if(answer != null){
                answers.add(answer)
            }
        }
        return Question(
            questionMap[questionIdPath] as String,
            questionMap[courseIdPath] as String,
            questionMap[userIdPath] as String,
            questionMap[questionTextPath] as String,
            answers)
    }

    private fun getAnswer(dataSnapshot: DataSnapshot): Answer?{
        if(dataSnapshot.value == null){
            return null
        }
        // save every variables of the answer in a map
        val answerMap = hashMapOf<String, Any?>()
        dataSnapshot.children.forEach { answerMap[it.key!!] = it.value }

        return Answer(answerMap[answerIdPath] as String,
            answerMap[questionIdPath] as String,
            answerMap[userIdPath] as String,
            answerMap[answerTextPath] as String)
    }

    private fun getCourse(dataSnapshot: DataSnapshot): Course?{
        if(dataSnapshot.value == null){
            return null
        }
        // save every non list variables of the course in a map
        val courseMap = hashMapOf<String, Any?>()
        dataSnapshot.children.forEach {
            if (it.key != questionsPath) {
                courseMap[it.key!!] = it.value
            }
        }
        // save every questions in a List using getQuestion private method
        val questions = arrayListOf<Question>()
        dataSnapshot.child(questionsPath).children.forEach {
            val question = getQuestion(it)
            if(question != null){
                questions.add(question)
            }
        }

        return Course(courseMap[courseIdPath] as String,
            courseMap[courseNamePath] as String,
            questions)
    }

}