package com.github.ybecker.epforuml.database

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDateTime

class Model {

    // This class represent a Question
    data class Question(
        val questionId: String,
        val courseId: String,
        val userId: String,
        val questionTitle: String,
        val questionText: String,
        val imageURI : String,
        var answers: List<String>
        ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.createStringArrayList()!!
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(questionId)
            parcel.writeString(courseId)
            parcel.writeString(userId)
            parcel.writeString(questionTitle)
            parcel.writeString(questionText)
            parcel.writeString(imageURI)
            parcel.writeStringList(answers)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Question> {
            override fun createFromParcel(parcel: Parcel): Question {
                return Question(parcel)
            }

            override fun newArray(size: Int): Array<Question?> {
                return arrayOfNulls(size)
            }
        }

    }

    // This class represent a user an answer
    data class Answer(val answerId: String, val questionId: String, val userId: String, val answerText: String)
        : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!
        ) {
        }

        constructor() : this("", "", "", "")

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(answerId)
            parcel.writeString(questionId)
            parcel.writeString(userId)
            parcel.writeString(answerText)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Answer> {
            override fun createFromParcel(parcel: Parcel): Answer {
                return Answer(parcel)
            }

            override fun newArray(size: Int): Array<Answer?> {
                return arrayOfNulls(size)
            }
        }
    }

    // This class represent a user
    data class User(
        val userId: String,
        var username: String,
        var email: String = "",
        var questions: List<String> = emptyList(),
        var answers: List<String> = emptyList(),
        var subscriptions: List<String> = emptyList(),
        var profilePic: String = "",
        var userInfo: String = "",
        var status: String = "",
        ) {
        constructor() : this(
            "",
            "",
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            "",
            "",
            ""
        )
    }

    //This class represent a course
    data class Course(val courseId: String, val courseName: String, var questions: List<String>){
        constructor() : this("", "", emptyList())
    }

    data class Chat(
        val chatId: String?, val date: LocalDateTime?,val receiverId:String, val senderId:String,  val text: String?){
    }
}