package com.github.ybecker.epforuml

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.github.ybecker.epforuml.database.Model

class QuestionDetailsActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_details)

        val question = intent.getParcelableExtra("question", Model.Question::class.java)
        if (question != null) {
            val textView : TextView = findViewById(R.id.question_details_main_text)
            textView.text = question.questionText
        }
    }
}