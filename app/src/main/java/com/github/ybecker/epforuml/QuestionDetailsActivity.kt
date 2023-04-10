package com.github.ybecker.epforuml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.Model

class QuestionDetailsActivity : AppCompatActivity() {

    private lateinit var answerAdapter : AnswerAdapter
    private lateinit var answerRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_details)

        val originFragment = intent.getStringExtra("originFragment")

        val button : Button = findViewById(R.id.back_to_forum_button)
        button.setOnClickListener{ // Create an intent to return to the previous fragment
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        val sendButton : CardView =  findViewById(R.id.post_reply_button)
        sendButton.setOnClickListener(sendReply())


        answerRecyclerView = findViewById(R.id.answers_recycler)
        answerRecyclerView.layoutManager = LinearLayoutManager(this)
        answerRecyclerView.setHasFixedSize(true)

        val question = intent.getParcelableExtra<Model.Question>("question")
        if (question != null) {
            val title : TextView = findViewById(R.id.qdetails_title)
            title.text = question.questionTitle

            answerRecyclerView.adapter = AnswerAdapter(question.questionId, question.questionText, question.answers)
        }
    }

    private fun sendReply() {

    }
}