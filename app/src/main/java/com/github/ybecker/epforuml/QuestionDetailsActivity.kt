package com.github.ybecker.epforuml

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.MainActivity.Companion.context
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
            startActivity(Intent(this, MainActivity::class.java))
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

        db.getQuestionFollowers(questionId).thenAccept {
            val notificationButton = findViewById<ImageButton>(R.id.addFollowButton)
            val endorsementCounter = findViewById<TextView>(R.id.notificationCount)
            val count = it.size

            if(user == null || user.userId.isEmpty()){
                notificationButton.isEnabled = false
            }
            endorsementCounter.text = (count).toString()

            val notificationActive = it.contains(user.userId)
            notificationButton.tag = listOf(notificationActive, count)

            if(notificationActive){
                notificationButton.setColorFilter(ContextCompat.getColor(context, R.color.yellow), PorterDuff.Mode.SRC_IN)
            } else {
                notificationButton.setColorFilter(ContextCompat.getColor(context, R.color.light_gray), PorterDuff.Mode.SRC_IN)
            }

            notificationButton.setOnClickListener {
                val tags = notificationButton.tag as List<*>
                val isActive = tags[0] as Boolean
                val count = tags[1] as Int
                if (!isActive) {
                    db.addQuestionFollower(user.userId, questionId)
                    val newCount = count+1
                    endorsementCounter.text = (newCount).toString()
                    notificationButton.tag = listOf(true, newCount)
                    notificationButton.setColorFilter(ContextCompat.getColor(context, R.color.yellow), PorterDuff.Mode.SRC_IN)
                } else {
                    db.removeQuestionFollower(user.userId, questionId)
                    val newCount = count-1
                    endorsementCounter.text =(newCount).toString()
                    notificationButton.tag = listOf(false, newCount)
                    notificationButton.setColorFilter(ContextCompat.getColor(context, R.color.light_gray), PorterDuff.Mode.SRC_IN)
                }
            }
        }

        // only allow posting answer if user is connected
        if (user != null && user.userId.isNotEmpty()) {
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
            answerRecyclerView.adapter = AnswerAdapter(question!!, this)
        }

    }

    private fun reloadQuestion() {
        db.getQuestionById(questionId).thenAccept {
            question = it
        }
    }
}