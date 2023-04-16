package com.github.ybecker.epforuml

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model

class QuestionDetailsActivity : AppCompatActivity() {

    private lateinit var answerRecyclerView: RecyclerView
    private var question : Model.Question? = null
    private lateinit var questionId : String

    private lateinit var user : Model.User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val button : Button = findViewById(R.id.back_to_forum_button)
        button.setOnClickListener{ // Create an intent to return to the previous fragment
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        answerRecyclerView = findViewById(R.id.answers_recycler)
        answerRecyclerView.layoutManager = LinearLayoutManager(this)

        // create answer view
        question = intent.getParcelableExtra("question")
        questionId = question!!.questionId
        val title : TextView = findViewById(R.id.qdetails_title)
        title.text = question!!.questionTitle
        updateRecycler()

        user = DatabaseManager.user ?: Model.User()
        val replyBox : EditText = findViewById(R.id.write_reply_box)
        val sendButton : ImageButton =  findViewById(R.id.post_reply_button)

        db.getQuestionEndorsements(questionId).thenAccept {
            val endorsementButton = findViewById<ToggleButton>(R.id.endorsementButton)
            val endorsementCounter = findViewById<TextView>(R.id.endorsementCount)
            val count = it.size

            endorsementCounter.text = (count).toString()

            endorsementButton.tag = count
            endorsementButton.isChecked = it.contains(user.userId)

            endorsementButton.setOnClickListener {
                val count = endorsementButton.tag as Int
                if (endorsementButton.isChecked) {
                    db.addQuestionEndorsement(user.userId, questionId)
                    val newCount = count+1
                    endorsementCounter.text = (newCount).toString()
                    endorsementButton.tag = newCount
                } else {
                    db.removeQuestionEndorsement(user.userId, questionId)
                    val newCount = count-1
                    endorsementCounter.text =(newCount).toString()
                    endorsementButton.tag = newCount
                }
            }
        }


        // only allow posting answer if user is connected
        if (user.userId.isNotEmpty()) {
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
            val cardView : CardView = findViewById(R.id.write_reply_card)
            cardView.visibility = View.GONE
            sendButton.visibility = View.GONE

            val textView : TextView = findViewById(R.id.not_loggedin_text)
            textView.visibility = View.VISIBLE
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