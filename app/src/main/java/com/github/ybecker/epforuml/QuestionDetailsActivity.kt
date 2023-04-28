package com.github.ybecker.epforuml

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model

class QuestionDetailsActivity : AppCompatActivity() {

    private lateinit var answerRecyclerView: RecyclerView
    private var question : Model.Question? = null
    private lateinit var questionId : String

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var user : Model.User
    private lateinit var userId : String

    private lateinit var saveToggle : ImageButton

    private lateinit var cache : ArrayList<Model.Question>

    private lateinit var newIntent : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // retrieve cache value
        cache = intent.getParcelableArrayListExtra("savedQuestions")!!

        newIntent = Intent(
            this,
            MainActivity::class.java
        )

        newIntent.putExtra("fragment", "HomeFragment")
        updateNewIntent()

        val button : Button = findViewById(R.id.back_to_forum_button)
        button.setOnClickListener{ // Create an intent to return to the previous fragment
            startActivity(newIntent)
            finish()
        }

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)

        swipeRefreshLayout.setOnRefreshListener {
            // Reload data from database and update adapter
            updateRecycler()
            // Once the refresh is complete, call setRefreshing(false) to hide the loading indicator
            swipeRefreshLayout.isRefreshing = false
        }

        swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.purple_500)
        )

        answerRecyclerView = findViewById(R.id.answers_recycler)
        answerRecyclerView.layoutManager = LinearLayoutManager(this)

        // create answer view
        question = intent.getParcelableExtra("question")
        questionId = question!!.questionId
        val title : TextView = findViewById(R.id.qdetails_title)
        title.text = question!!.questionTitle
        updateRecycler()

        // retrieve user
        user = DatabaseManager.user ?: Model.User()
        userId = user.userId
        val replyBox : EditText = findViewById(R.id.write_reply_box)
        val sendButton : ImageButton =  findViewById(R.id.post_reply_button)

        db.getQuestionEndorsements(questionId).thenAccept {
            val endorsementButton = findViewById<ToggleButton>(R.id.endorsementButton)
            val endorsementCounter = findViewById<TextView>(R.id.endorsementCount)
            val count = it.size

            if(userId.isEmpty()){
                endorsementButton.isEnabled = false
            }
            endorsementCounter.text = (count).toString()

            endorsementButton.tag = count
            endorsementButton.isChecked = it.contains(userId)

            endorsementButton.setOnClickListener {
                val count = endorsementButton.tag as Int
                if (endorsementButton.isChecked) {
                    db.addQuestionEndorsement(userId, questionId)
                    val newCount = count+1
                    endorsementCounter.text = (newCount).toString()
                    endorsementButton.tag = newCount
                } else {
                    db.removeQuestionEndorsement(userId, questionId)
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
                if (question != null) {
                    val replyText : String = replyBox.text.toString()

                    // allow only non-empty answers
                    if (replyText != "") {
                        replyBox.setText("")

                        db.addAnswer(userId, question!!.questionId, replyText)
                        updateRecycler()
                    }
                }
            }

            saveToggle = findViewById(R.id.toggle_save_question)
            switchImageButton()

            saveToggle.setOnClickListener {
                // question is saved, will be unsaved after click
                //if (questionIsSaved) {
                if (isSavedQuestion()) {
                    cache.remove(question!!)
                }
                // question is not yet saved, will be saved after click
                else {
                    cache.add(question!!)
                }

                updateNewIntent()
                switchImageButton()
            }
        } else {
            val cardView : CardView = findViewById(R.id.write_reply_card)
            cardView.visibility = View.GONE
            sendButton.visibility = View.GONE

            val textView : TextView = findViewById(R.id.not_loggedin_text)
            textView.visibility = View.VISIBLE

            val saveButton : ImageButton = findViewById(R.id.toggle_save_question)
            saveButton.visibility = View.GONE
        }

    }


    private fun updateRecycler() {
        db.getQuestionById(questionId).thenAccept {
            question = it
            answerRecyclerView.adapter = AnswerAdapter(question!!.questionId, question!!.questionText, question!!.answers, this)
        }
    }

    private fun isSavedQuestion(): Boolean {
        return cache.contains(question)
    }

    private fun switchImageButton() {
        saveToggle.setBackgroundResource(
            when(isSavedQuestion()) {
                true -> R.drawable.checkmark
                false -> R.drawable.nav_saved_questions
            }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(newIntent)
        }

        return true
    }


    private fun updateNewIntent() {
        newIntent.putParcelableArrayListExtra("savedQuestions", cache)
    }

}