package com.github.ybecker.epforuml.database

class Model {

    // This class represent a Question

    data class Question(val questionId: String, val courseId: String, val userId: String, val questionTitle: String, val questionText: String, val imageURI : String, var answers: List<String>){
        constructor() : this("", "", "", "", "", "", emptyList())

    }

    // This class represent a user an answer
    data class Answer(val answerId: String, val questionId: String, val userId: String, val answerText: String){
        constructor() : this("", "", "", "")
    }

    // This class represent a user
    data class User(val userId: String, val username: String, var questions: List<String>, var answers: List<String>, var subscriptions: List<String>){
        constructor() : this("", "", emptyList(), emptyList(), emptyList())
    }

    //This class represent a course
    data class Course(val courseId: String, val courseName: String, var questions: List<String>){
        constructor() : this("", "", emptyList())
    }
}