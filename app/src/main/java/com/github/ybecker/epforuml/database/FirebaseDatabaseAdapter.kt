package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.database.Model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * This class represents a database that uses Firebase Realtime Database
 */
class FirebaseDatabaseAdapter : Database() {

    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val usersPath = "users"
    private val coursesPath = "courses"
    private val questionsPath = "questions"
    private val answerPath = "answers"

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

        db.child(coursesPath).get().addOnSuccessListener {
            val courses = mutableSetOf<Course>()
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

    //Note that using course.questions is false because you don't take the db last value !
    override fun getCourseQuestions(course: Course): Set<Question> {
        val future = CompletableFuture<Set<Question>>()
        db.child(coursesPath).child(course.courseId).get().addOnSuccessListener {
            val questions = mutableSetOf<Question>()
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

    override fun getQuestionAnswers(question: Question): Set<Answer> {

        val future = CompletableFuture<Set<Answer>>()
        db.child(questionsPath).child(question.questionId).get().addOnSuccessListener {
            val answers = mutableSetOf<Answer>()
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

    override fun getUserQuestions(user: User): Set<Question> {
        val future = CompletableFuture<Set<Question>>()
        db.child(usersPath).child(user.userId).get().addOnSuccessListener {
            val questions = mutableSetOf<Question>()
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

    override fun addQuestion(user: User, course: Course, questionText: String?): Question {
        val newChildRef = db.child(questionsPath).push()
        val questionId = newChildRef.key ?: error("Failed to generate question ID")
        val question = Question(questionId, course.courseId, user.userId, questionText ?: "", emptyList())
        newChildRef.setValue(question)
        db.child(questionsPath).child(question.questionId).setValue(question)

        val updatedCourseQuestions = course.questions + question
        val courseUpdates = hashMapOf<String, Any>(questionsPath to updatedCourseQuestions)
        db.child(coursesPath).child(course.courseId).updateChildren(courseUpdates)

        val updatedUserQuestions = user.questions + question
        val userUpdates = hashMapOf<String, Any>(questionsPath to updatedUserQuestions)
        db.child(usersPath).child(user.userId).updateChildren(userUpdates)

        return question
    }

    override fun addAnswer(user: User, question: Question, answerText: String?): Answer {
        val newChildRef = db.child(answerPath).push()
        val answerId = newChildRef.key ?: error("Failed to generate question ID")
        val answer = Answer(answerId, question.questionId, user.userId, answerText ?: "")
        newChildRef.setValue(answer)
        db.child(answerPath).child(answer.answerId).setValue(answer)

        val updatedQuestionAnswer = question.answers + answer
        val questionUpdates = hashMapOf<String, Any>(answerPath to updatedQuestionAnswer)
        db.child(questionsPath).child(question.questionId).updateChildren(questionUpdates)

        val updatedUserAnswer = user.answers + answer
        val userUpdates = hashMapOf<String, Any>(answerPath to updatedUserAnswer)
        db.child(usersPath).child(user.userId).updateChildren(userUpdates)

        return answer
    }

    //TODO rework this with one single id per user (I wait the authentification part to be done for that)
    override fun addUser(username: String): User {
        val newChildRef = db.child(usersPath).push()
        val userId = newChildRef.key ?: error("Failed to generate user ID")
        val user = User(userId, username, emptyList(), emptyList())
        newChildRef.setValue(user)
        return user
    }

    override fun getQuestionById(id: String): Question? {
        val future = CompletableFuture<Question?>()

        db.child(questionsPath).child(id).get().addOnSuccessListener {
            future.complete(getQuestion(it))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future.get(5,TimeUnit.SECONDS)
    }

    override fun getAnswerById(id: String): Answer? {
        val future = CompletableFuture<Answer?>()

        db.child(answerPath).child(id).get().addOnSuccessListener {
            future.complete(getAnswer(it))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future.get(5,TimeUnit.SECONDS)
    }

    override fun getUserById(id: String): User? {
        val future = CompletableFuture<User?>()

        db.child(usersPath).child(id).get().addOnSuccessListener {
            future.complete(getUser(it))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future.get(5,TimeUnit.SECONDS)
    }

    override fun getCourseById(id: String): Course? {
        val future = CompletableFuture<Course?>()

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

        val userMap = hashMapOf<String, Any?>()

        dataSnapshot.children.forEach {
            if (it.key != questionsPath && it.key!=answerPath) {
                userMap[it.key!!] = it.value
            }
        }

        val answers = arrayListOf<Answer>()
        dataSnapshot.child(answerPath).children.forEach {
            val answer = getAnswer(it)
            if(answer != null){
                answers.add(answer)
            }
        }

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

        var questionMap = hashMapOf<String, Any?>()

        dataSnapshot.children.forEach {
            if (it.key != answerPath) {
                questionMap[it.key!!] = it.value
            }
        }

        val answers = arrayListOf<Answer>()
        dataSnapshot.child(answerPath).children.forEach {
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

        val courseMap = hashMapOf<String, Any?>()

        dataSnapshot.children.forEach {
            if (it.key != questionsPath) {
                courseMap[it.key!!] = it.value
            }
        }

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