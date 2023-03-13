package com.github.ybecker.epforuml.database.entities

data class Question (val userId : Int, val questionId : Int, val courseId : Int, val title : String, val text :String)