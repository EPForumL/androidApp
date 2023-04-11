package com.github.ybecker.epforuml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model

class QuestionDetailsActivity : AppCompatActivity() {

    private lateinit var answerAdapter : AnswerAdapter
    private lateinit var answerRecyclerView: RecyclerView
    private var question : Model.Question? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_details)

        //val originFragment = intent.getStringExtra("originFragment")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val button : Button = findViewById(R.id.back_to_forum_button)
        button.setOnClickListener{ // Create an intent to return to the previous fragment
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        answerRecyclerView = findViewById(R.id.answers_recycler)
        answerRecyclerView.layoutManager = LinearLayoutManager(this)
        answerRecyclerView.setHasFixedSize(true)

        if (question != null) {
            val title : TextView = findViewById(R.id.qdetails_title)
            title.text = question!!.questionTitle

            updateRecycler(question!!)
        }

        val sendButton : CardView =  findViewById(R.id.post_reply_button)
        val replyBox : EditText = findViewById(R.id.write_reply_box)
        // store content of box as a new answer to corresponding question
        sendButton.setOnClickListener {
            if (question != null) {
                val replyText : String =  replyBox.text.toString()
                replyBox.setText("")

                // TODO : retrieve userId
                db.addAnswer("user", question!!.questionId, replyText)
                updateRecycler(question!!)
            } else {
                Toast.makeText(applicationContext, "Could not load this question.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun updateRecycler(question : Model.Question) {
        answerRecyclerView.adapter = AnswerAdapter(question.questionId, question.questionText, question.answers)
    }
}