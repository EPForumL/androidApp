package com.github.ybecker.epforuml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.ybecker.epforuml.database.Model

class QuestionDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_details)

        val originFragment = intent.getStringExtra("originFragment")

        val button : Button = findViewById(R.id.back_to_forum_button)
        button.setOnClickListener{ // Create an intent to return to the previous fragment


        startActivity(Intent(applicationContext, MainActivity::class.java))
    }

        val question = intent.getParcelableExtra<Model.Question>("question")
        if (question != null) {
            val textView : TextView = findViewById(R.id.qdetails_content)
            textView.text = question.questionText

            val title : TextView = findViewById(R.id.qdetails_title)
            title.text = question.questionTitle

            // TODO : implement RecyclerView for answers
            // var answerDisplay : RecyclerView = findViewById(R.id.answers_recycler)
        }
    }
}