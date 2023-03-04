package com.github.ybecker.epforuml

class Model {
    // This class represent a Question
    data class Question(val questionId: String, val courseId: String, val userId: String, val questionText: String, var answers: List<Answer>)

    // This class represent a user an answer
    data class Answer(val answerId: String, val questionId: String, val userId: String, val answerText: String)

    // This class represent a user
    data class User(val userId: String, val username: String, var answers: List<Question>)

    //This class represent a course
    data class Course(val courseId: String, val courseName: String, var answers: List<Question>)
}