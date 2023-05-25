package com.github.ybecker.epforuml

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.MainActivity.Companion.context
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.ybecker.epforuml.MainActivity.Companion.saveDataToDevice
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import com.github.ybecker.epforuml.latex.LatexDialog
import com.github.ybecker.epforuml.sensor.AndroidAudioPlayer
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

class QuestionDetailsActivity : AppCompatActivity() {

    private lateinit var answerRecyclerView: RecyclerView
    private var question : Model.Question? = null
    private lateinit var questionId : String

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var user : Model.User
    private lateinit var userId : String

    private lateinit var saveToggle : ImageButton

    private lateinit var newIntent : Intent
    private lateinit var comingFromFragment : String
    private lateinit var audioPlayer : AndroidAudioPlayer

    private lateinit var username :String

    private lateinit var cache : ArrayList<Model.Question>
    private var answersCache : ArrayList<Model.Answer> = arrayListOf()

    private lateinit var allQuestionsCache : ArrayList<Model.Question>
    private lateinit var allAnswersCache : ArrayList<Model.Answer>
    private lateinit var allCoursesCache : ArrayList<Model.Course>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_details)

        audioPlayer = AndroidAudioPlayer(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // retrieve cache value
        cache = intent.getParcelableArrayListExtra("savedQuestions")!!
        answersCache = intent.getParcelableArrayListExtra("savedAnswers")!!
        comingFromFragment = intent.getStringExtra("comingFrom")!!

        // TODO : check
        allQuestionsCache = intent.getParcelableArrayListExtra("allQuestions")!!
        allAnswersCache = intent.getParcelableArrayListExtra("allAnswers")!!
        allCoursesCache = intent.getParcelableArrayListExtra("allCourses")!!

        newIntent = Intent(
            this,
            MainActivity::class.java
        )

        newIntent.putExtra("fragment", comingFromFragment)
        newIntent.putParcelableArrayListExtra("savedAnswers", answersCache)
        updateNewIntent()

        hideContentWhenNotConnectedToInternet()

        answerRecyclerView = findViewById(R.id.answers_recycler)
        answerRecyclerView.layoutManager = LinearLayoutManager(this)

        // create answer view
        question = intent.getParcelableExtra("question")
        questionId = question!!.questionId
        val title : TextView = findViewById(R.id.qdetails_title)
        title.text = question!!.questionTitle

        db.getUserById(question!!.userId).thenAccept {

            if(question?.isAnonymous!!){
                username = DatabaseManager.anonymousUsers[Random.nextInt(0, DatabaseManager.anonymousUsers.size)]
            } else {
                username = it?.username!!
            }
            findViewById<TextView>(R.id.qdetails_question_username).text = getString(R.string.qdetail_username_text).replace("Username", username)
        }

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)

        swipeRefreshLayout.setOnRefreshListener {
            // Reload data from database and update adapter
            updateRecycler()
            hideContentWhenNotConnectedToInternet()
            // Once the refresh is complete, call setRefreshing(false) to hide the loading indicator
            swipeRefreshLayout.isRefreshing = false
        }

        swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.highlight)
        )

        updateRecycler()

        // retrieve user
        user = DatabaseManager.user ?: Model.User()
        userId = user.userId

        //setUpImage()
        endorsementSetup()

        // only allow posting answer if user is connected
        answerPostingSetup()
        setVoiceNoteButton()
    }


    private fun updateRecycler() {
        if (MainActivity.isConnected()) {
            val futureQuestions = db.getQuestions()
            val futureAnswers = db.getAllAnswers()
            val futureCurrentQuestion = db.getQuestionById(questionId)

            CompletableFuture
                .allOf(futureQuestions, futureAnswers, futureCurrentQuestion)
                .thenAccept {
                    val q = futureCurrentQuestion.get()
                    if(!q?.isAnonymous!!){
                        answerRecyclerView.adapter = AnswerAdapter(q, hashMapOf(),this)
                    } else {
                        answerRecyclerView.adapter = AnswerAdapter(q, hashMapOf(Pair(q.userId, username)),this)
                    }

                    saveDataToDevice(
                        cache,
                        answersCache,
                        futureQuestions.get() as ArrayList<Model.Question>,
                        futureAnswers.get() as ArrayList<Model.Answer>,
                        allCoursesCache
                    )
            }
        } else {
            answerRecyclerView.adapter = SavedAnswerAdapter(questionId, question!!.questionText, allAnswersCache)
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

        updateCacheIfConnected()
    }

    private fun updateCacheIfConnected() {
        if (MainActivity.isConnected()) {
            answersCache.clear()

            db.getAllAnswers().thenAccept {
                answersCache.addAll(it)
                newIntent.putParcelableArrayListExtra("savedAnswers", answersCache)
                saveDataToDevice(cache, answersCache, allQuestionsCache, allAnswersCache, allCoursesCache)
            }
        }
    }
    private fun setVoiceNoteButton(){
        val playButton = findViewById<Button>(R.id.play_note_button)
        if(question!!.audioPath == "null" || question!!.audioPath == ""){
            playButton.visibility = View.GONE
        }
        playButton.setOnClickListener{
           audioPlayer.playFile(Uri.parse(question!!.audioPath))
        }
    }
    private fun endorsementSetup() {
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
                    db.removeQuestionFollower(user.userId, questionId)
                    val newCount = count-1
                    followButton.text =(newCount).toString()
                    notificationButton.tag = listOf(false, newCount)
                    notificationButton.setColorFilter(ContextCompat.getColor(context, R.color.light_gray), PorterDuff.Mode.SRC_IN)
                }
            }
        }
    }



    private fun answerPostingSetup() {
        val replyBox : EditText = findViewById(R.id.write_reply_box)
        val sendButton : ImageButton = findViewById(R.id.post_reply_button)
        val latexButton : ImageButton = findViewById(R.id.question_details_latex)

        if (userId.isNotEmpty()) {
            latexButton.setOnClickListener { LatexDialog(this, replyBox).show() }

            // store content of box as a new answer to corresponding question
            sendButton.setOnClickListener {
                if (question != null) {
                    val replyText : String = replyBox.text.toString()
                    // allow only non-empty answers
                    if (replyText != "") {
                        replyBox.setText("")
                        db.addAnswer(userId, question!!.questionId, replyText)
                        updateRecycler()
                        updateNewIntent()
                    }
                }
            }
            saveToggle = findViewById(R.id.toggle_save_question)
            switchImageButton()
            saveToggle.setOnClickListener {
                // question is saved, will be unsaved after click
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
            latexButton.visibility =View.GONE

            val textView : TextView = findViewById(R.id.not_loggedin_text)
            textView.visibility = View.VISIBLE

            val saveButton : ImageButton = findViewById(R.id.toggle_save_question)
            saveButton.visibility = View.GONE
        }
    }

    private fun hideContentWhenNotConnectedToInternet() {
        val cardView : CardView = findViewById(R.id.write_reply_card)
        val sendButton : ImageButton = findViewById(R.id.post_reply_button)
        val saveButton : ImageButton = findViewById(R.id.toggle_save_question)
        val latexButton : ImageButton = findViewById(R.id.question_details_latex)

        if (!MainActivity.isConnected()) {
            cardView.visibility = View.GONE
            sendButton.visibility = View.GONE
            saveButton.visibility = View.GONE
            latexButton.visibility = View.GONE
        } else {
            cardView.visibility = View.VISIBLE
            sendButton.visibility = View.VISIBLE
            saveButton.visibility = View.VISIBLE
            latexButton.visibility = View.VISIBLE
        }
    }


}