package com.github.ybecker.epforuml.database.entities

data class Answer (val answerId : Int, val questionId: Int, val text: String, val userId : Int)