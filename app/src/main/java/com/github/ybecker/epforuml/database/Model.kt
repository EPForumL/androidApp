package com.github.ybecker.epforuml.database

import android.os.Build
import android.os.Parcel
import android.os.Parcelable

import androidx.annotation.RequiresApi

class Model {

    // This class represent a Question
    data class Question(
        val questionId: String,
        val courseId: String,
        val userId: String,
        val isAnonymous: Boolean,
        val questionTitle: String,
        val questionText: String,
        val imageURI : String,
        var answers: List<String>,
        var followers: List<String>,
        val audioPath : String
        ) : Parcelable {
        @RequiresApi(Build.VERSION_CODES.Q)
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readBoolean(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.createStringArrayList()!!,
            parcel.createStringArrayList()!!,
            parcel.readString()!!
            )

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(questionId)
            parcel.writeString(courseId)
            parcel.writeString(userId)
            parcel.writeBoolean(isAnonymous)
            parcel.writeString(questionTitle)
            parcel.writeString(questionText)
            parcel.writeString(imageURI)
            parcel.writeStringList(answers)
            parcel.writeStringList(followers)
            // At some point audioPath might be null.
            parcel.writeString(audioPath)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Question> {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun createFromParcel(parcel: Parcel): Question {
                return Question(parcel)
            }

            override fun newArray(size: Int): Array<Question?> {
                return arrayOfNulls(size)
            }
        }

    }

    // This class represent a user an answer
    data class Answer(
            val answerId: String,
            val questionId: String,
            val userId: String,
            val answerText: String,
            var like: List<String>,
            val endorsed: String
        )
        : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.createStringArrayList()!!,
            parcel.readString()!!
        )

        constructor() : this("", "", "", "", emptyList(),"")

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(answerId)
            parcel.writeString(questionId)
            parcel.writeString(userId)
            parcel.writeString(answerText)
            parcel.writeStringList(like)
            parcel.writeString(endorsed)
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
        var notifications: List<String> = emptyList(),
        var chatsWith: List<String> = emptyList(),
        var profilePic: String = "",
        var userInfo: String = "",
        var status: List<String> = emptyList(),
        val connections: ArrayList<Boolean> = ArrayList(),
        var sharesLocation: Boolean = false,
        var longitude: Double = -200.0,
        var latitude: Double = -200.0
        ) {
        constructor() : this(
            "",
            "",
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            "",
            "",
            emptyList(),
            ArrayList(),
            false,
            -200.0,
            -200.0
        )
    }

    //This class represent a course
    data class Course(
            val courseId: String,
            val courseName: String,
            var questions: List<String>,
            var notifications: List<String>
        ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.createStringArrayList()!!,
            parcel.createStringArrayList()!!
        )

        constructor() : this("", "", emptyList(), emptyList())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(courseId)
            parcel.writeString(courseName)
            parcel.writeStringList(questions)
            parcel.writeStringList(notifications)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Course> {
            override fun createFromParcel(parcel: Parcel): Course {
                return Course(parcel)
            }

            override fun newArray(size: Int): Array<Course?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class Chat(

        val chatId: String?, val date: String?,val receiverId:String, val senderId:String,  val text: String?)
}