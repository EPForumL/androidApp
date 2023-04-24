package com.github.ybecker.epforuml

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
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
    private lateinit var mQuestion : Model.Question
    private lateinit var mQuestionId : String

    private lateinit var user : Model.User
    private lateinit var userId : String

    private lateinit var saveToggle : ImageButton

    private lateinit var cache : ArrayList<Model.Question>

    private lateinit var newIntent : Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        answerRecyclerView = findViewById(R.id.answers_recycler)
        answerRecyclerView.layoutManager = LinearLayoutManager(this)

        // create answer view
        mQuestion = intent.getParcelableExtra("question")!!
        mQuestionId = mQuestion.questionId
        val title : TextView = findViewById(R.id.qdetails_title)
        title.text = mQuestion.questionTitle
        updateRecycler()

        // retrieve cache value
        cache = intent.getParcelableArrayListExtra("savedQuestions")!!


        // create new intent to resend to home (mandatory)
        newIntent = Intent(applicationContext, MainActivity::class.java)
        newIntent.putParcelableArrayListExtra("savedQuestions", cache)

        // load user
        user = DatabaseManager.user ?: Model.User()
        userId = user.userId
        val replyBox : EditText = findViewById(R.id.write_reply_box)
        val sendButton : ImageButton =  findViewById(R.id.post_reply_button)

        db.getQuestionEndorsements(mQuestionId).thenAccept {
            val endorsementButton = findViewById<ToggleButton>(R.id.endorsementButton)
            val endorsementCounter = findViewById<TextView>(R.id.endorsementCount)
            val count = it.size

            if(userId.isEmpty()){
                endorsementButton.isEnabled = false
            }
            endorsementCounter.text = (count).toString()

            endorsementButton.tag = count
            endorsementButton.isChecked = it.contains(user.userId)

            endorsementButton.setOnClickListener {
                val count = endorsementButton.tag as Int
                if (endorsementButton.isChecked) {
                    db.addQuestionEndorsement(user.userId, mQuestionId)
                    val newCount = count+1
                    endorsementCounter.text = (newCount).toString()
                    endorsementButton.tag = newCount
                } else {
                    db.removeQuestionEndorsement(user.userId, mQuestionId)
                    val newCount = count-1
                    endorsementCounter.text =(newCount).toString()
                    endorsementButton.tag = newCount
                }
            }
        }


        // only allow posting answer if user is connected

        if (userId.isNotEmpty()) {
            // store content of box as a new answer to corresponding question
            sendButton.setOnClickListener {
                val replyText : String =  replyBox.text.toString()

                // allow only non-empty answers
                if (replyText != "") {
                    replyBox.setText("")

                    db.addAnswer(userId, mQuestionId, replyText)
                    updateRecycler()
                }
            }

            // save question button
            saveToggle = findViewById(R.id.toggle_save_question)
            switchImageButton()

            // TODO : fix save upon click
            saveToggle.setOnClickListener {
                // question is saved, will be unsaved after click
                //if (questionIsSaved) {
                if (isSavedQuestion()) {
                    cache.remove(mQuestion)
                    update()
                }
                // question is not yet saved, will be saved after click
                else {
                    cache.add(mQuestion)
                    update()
                }

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
        db.getQuestionById(mQuestionId).thenAccept {
            mQuestion = it!!
            answerRecyclerView.adapter = AnswerAdapter(mQuestionId, mQuestion.questionText, mQuestion!!.answers, this)
        }
    }

    private fun isSavedQuestion(): Boolean {
        return cache.contains(mQuestion)
    }

    private fun switchImageButton() {
        saveToggle.setBackgroundResource(
            when(isSavedQuestion()) {
                true -> R.drawable.checkmark
                false -> R.drawable.nav_saved_questions
            }
        )
    }
/*
    private fun savedBecomesInverse() {
        questionIsSaved = !questionIsSaved
    }

 */

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(newIntent)
        }

        return true
    }


    private fun updateNewIntent() {
        newIntent.putParcelableArrayListExtra("savedQuestions", cache)
    }

    private fun update() {
        updateNewIntent()
        //savedBecomesInverse()
    }
}