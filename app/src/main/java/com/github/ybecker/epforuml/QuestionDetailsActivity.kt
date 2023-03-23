package com.github.ybecker.epforuml

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.Model

class QuestionDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_details)

        val question = intent.getParcelableExtra<Model.Question>("question")
        if (question != null) {
            val textView : TextView = findViewById(R.id.qdetails_content)
            textView.text = question.questionText

            val title : TextView = findViewById(R.id.qdetails_title)
            title.text = question.questionId

            // TODO : implement RecyclerView for answers
            // var answerDisplay : RecyclerView = findViewById(R.id.answers_recycler)
        }
    }
}