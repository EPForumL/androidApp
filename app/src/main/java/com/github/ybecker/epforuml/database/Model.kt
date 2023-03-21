package com.github.ybecker.epforuml.database

class Model {

    // This class represent a Question
    data class Question(val questionId: String, val courseId: String, val userId: String, val questionText: String, val imageURI : String, var answers: List<Answer>){
        constructor() : this("", "", "", "", "",emptyList())
    }

    // This class represent a user an answer
    data class Answer(val answerId: String, val questionId: String, val userId: String, val answerText: String){
        constructor() : this("", "", "", "")
    }

    // This class represent a user
    data class User(val userId: String, val username: String, var questions: List<Question>, var answers: List<Answer>, var subscriptions: List<Course>){
        constructor() : this("", "", emptyList(), emptyList(), emptyList())
    }

    //This class represent a course
    data class Course(val courseId: String, val courseName: String, var questions: List<Question>){
        constructor() : this("", "", emptyList())
    }
}