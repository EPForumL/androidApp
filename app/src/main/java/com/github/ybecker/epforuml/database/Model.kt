package com.github.ybecker.epforuml.database

import android.os.Parcel
import android.os.Parcelable

class Model {

    // This class represent a Question
    data class Question(val questionId: String, val courseId: String, val userId: String, val questionTitle: String, val questionText: String, val imageURI : String, var answers: List<String>)
        : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.createTypedArrayList(Answer.CREATOR)!!
        ) {
        }

        constructor() : this("", "", "", "", "",emptyList())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(questionId)
            parcel.writeString(courseId)
            parcel.writeString(userId)
            parcel.writeString(questionText)
            parcel.writeString(imageURI)
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
    data class User(val userId: String, val username: String, var questions: List<String>, var answers: List<String>, var subscriptions: List<String>){
        constructor() : this("", "", emptyList(), emptyList(), emptyList())
    }

    //This class represent a course
    data class Course(val courseId: String, val courseName: String, var questions: List<String>){
        constructor() : this("", "", emptyList())
    }
}