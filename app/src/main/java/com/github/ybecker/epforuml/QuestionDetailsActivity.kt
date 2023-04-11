package com.github.ybecker.epforuml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model

class QuestionDetailsActivity : AppCompatActivity() {

    private lateinit var answerAdapter : AnswerAdapter
    private lateinit var answerRecyclerView: RecyclerView
    private var question : Model.Question? = null
    private lateinit var questionId : String

    private lateinit var user : Model.User

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

        question = intent.getParcelableExtra("question")
        if (question != null) {
            questionId = question!!.questionId
            val title : TextView = findViewById(R.id.qdetails_title)
            title.text = question!!.questionTitle

            updateRecycler()
        }


        user = DatabaseManager.user ?: Model.User()
        val replyBox : EditText = findViewById(R.id.write_reply_box)

        // only allow posting answer if user is connected
        if (user.userId.isNotEmpty()) {

            val sendButton : ImageButton =  findViewById(R.id.post_reply_button)
            // store content of box as a new answer to corresponding question
            sendButton.setOnClickListener {
                if (question != null) {
                    val replyText : String =  replyBox.text.toString()

                    // allow only non-empty answers
                    if (replyText != "") {
                        replyBox.setText("")

                        db.addAnswer(user.userId, question!!.questionId, replyText)
                        updateRecycler()
                    }
                }
            }

        } else {
            replyBox.setHint("Please login to post an answer.")
        }

    }


    private fun updateRecycler() {
        db.getQuestionById(questionId).thenAccept {
            question = it
            answerRecyclerView.adapter = AnswerAdapter(question!!.questionId, question!!.questionText, question!!.answers)
        }

    }

    private fun reloadQuestion() {
        db.getQuestionById(questionId).thenAccept {
            question = it
        }
    }
}