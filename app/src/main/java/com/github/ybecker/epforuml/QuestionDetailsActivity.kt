package com.github.ybecker.epforuml

import android.content.Intent
import android.net.Uri
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.MainActivity.Companion.context
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // retrieve cache value
        cache = intent.getParcelableArrayListExtra("savedQuestions")!!

        newIntent = Intent(
            this,
            MainActivity::class.java
        )

        newIntent.putExtra("fragment", "HomeFragment")
        updateNewIntent()

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

        setUpImage()
        db.getQuestionFollowers(questionId).thenAccept {
            val notificationButton = findViewById<ImageButton>(R.id.addFollowButton)
            val followButton = findViewById<TextView>(R.id.notificationCount)
            val count = it.size

            if(userId.isEmpty()){
                notificationButton.isEnabled = false

            }
            followButton.text = (count).toString()

            val notificationActive = it.contains(userId)
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
                    followButton.text = (newCount).toString()
                    notificationButton.tag = listOf(true, newCount)
                    notificationButton.setColorFilter(ContextCompat.getColor(context, R.color.yellow), PorterDuff.Mode.SRC_IN)
                } else {
                    db.removeQuestionFollower(userId, questionId)
                    db.removeQuestionFollower(user.userId, questionId)
                    val newCount = count-1
                    followButton.text =(newCount).toString()
                    notificationButton.tag = listOf(false, newCount)
                    notificationButton.setColorFilter(ContextCompat.getColor(context, R.color.light_gray), PorterDuff.Mode.SRC_IN)
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

    private fun setUpImage() {
        val image: ImageView = findViewById(R.id.image_question)
        if (question!!.imageURI == "") {
            image.visibility = View.GONE
        } else {
            image.visibility = View.VISIBLE
            displayImageFromFirebaseStorage(question!!.imageURI, image)
        }
    }

    private fun updateRecycler() {
        db.getQuestionById(questionId).thenAccept {
            question = it
            answerRecyclerView.adapter = AnswerAdapter(question!!, this)
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
            finish()
        }

        return true
    }


    private fun updateNewIntent() {
        newIntent.putParcelableArrayListExtra("savedQuestions", cache)
    }

    private fun displayImageFromFirebaseStorage(imageUrl: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
}