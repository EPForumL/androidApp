package com.github.ybecker.epforuml

import android.annotation.SuppressLint
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.cache.SavedQuestionsCache
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model

class QuestionDetailsActivity : AppCompatActivity() {

    private lateinit var answerRecyclerView: RecyclerView
    private var question : Model.Question? = null
    private lateinit var questionId : String

    private lateinit var user : Model.User
    private lateinit var userId : String

    private lateinit var saveToggle : ImageButton

    private lateinit var savedQuestions : SavedQuestionsCache
    private var bundle = Bundle()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // retrieve cache of saved questions
        savedQuestions = intent.getParcelableExtra("savedQuestions") ?: SavedQuestionsCache()
        updateBundle()
        //updateIntent()

        // enable back button
        val button : Button = findViewById(R.id.back_to_forum_button)
        button.setOnClickListener{ // Create an intent to return to the previous fragment
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        // enable answer view
        answerRecyclerView = findViewById(R.id.answers_recycler)
        answerRecyclerView.layoutManager = LinearLayoutManager(this)

        // create answer view
        question = intent.getParcelableExtra("question")
        questionId = question!!.questionId
        val title : TextView = findViewById(R.id.qdetails_title)
        title.text = question!!.questionTitle
        updateRecycler()

        user = DatabaseManager.user ?: Model.User()
        userId = user.userId
        val replyBox : EditText = findViewById(R.id.write_reply_box)
        val sendButton : ImageButton =  findViewById(R.id.post_reply_button)

        // only allow posting answer if user is connected
        if (userId.isNotEmpty()) {
            // store content of box as a new answer to corresponding question
            sendButton.setOnClickListener {
                if (question != null) {
                    val replyText : String =  replyBox.text.toString()

                    // allow only non-empty answers
                    if (replyText != "") {
                        replyBox.setText("")

                        db.addAnswer(userId, question!!.questionId, replyText)
                        updateRecycler()
                    }
                }
            }

            // save question button
            saveToggle = findViewById(R.id.toggle_save_question)
            switchImageButton()

            // TODO : fix toggle image
            saveToggle.setOnClickListener {
                // question is saved, will be unsaved after click
                if (checkSavedQuestion()) {
                    savedQuestions.remove(questionId)
                }
                // question is not yet saved, will be saved after click
                else {
                    savedQuestions.set(questionId, question!!)
                }

                // update cache to send
                updateBundle()
                //updateIntent()

                switchImageButton()
            }

        } else {
            val cardView : CardView = findViewById(R.id.write_reply_card)
            cardView.visibility = View.GONE
            sendButton.visibility = View.GONE

            val textView : TextView = findViewById(R.id.not_loggedin_text)
            textView.visibility = View.VISIBLE

            val saveLayout : LinearLayout = findViewById(R.id.save_question_layout)
            saveLayout.visibility = View.GONE
        }
    }


    private fun updateRecycler() {
        db.getQuestionById(questionId).thenAccept {
            question = it
            answerRecyclerView.adapter = AnswerAdapter(question!!.questionId, question!!.questionText, question!!.answers)
        }

    }

    private fun checkSavedQuestion(): Boolean {

        // TODO : fix
        savedQuestions.get(questionId) ?: return false

        return true
    }

    private fun switchImageButton() {
        saveToggle.setBackgroundResource(
            when(checkSavedQuestion()) {
                true -> R.drawable.checkmark
                false -> R.drawable.nav_saved_questions
            }
        )

    }

    private fun updateBundle() {
        bundle.putParcelable("savedQuestions", savedQuestions)
    }

    private fun updateIntent() {
        intent.putExtra("savedQuestions", savedQuestions)
    }
}