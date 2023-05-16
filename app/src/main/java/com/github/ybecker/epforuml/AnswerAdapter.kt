package com.github.ybecker.epforuml

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.DatabaseManager.user
import com.github.ybecker.epforuml.database.Model
//import katex.hourglass.`in`.mathlib.MathView
import com.github.ybecker.epforuml.latex.MathView
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

//private val questionId : String, private val questionText : String, private val answerList : List<String>,
class AnswerAdapter(private val question: Model.Question, private var anonymouseNameMap : HashMap<String, String>, private val mainActivity: Activity)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val HEADER_ITEM_TYPE = 0
        private const val CLASSIC_ITEM_TYPE = 1

        private const val HEADER_ITEM_COUNT = 1
        private const val HEADER_ITEM_POSITION = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            HEADER_ITEM_TYPE -> {
                HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.question_details_header_item, parent, false))

            }

            else -> {
                AnswerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.question_details_answer_item, parent, false))

            }
        }
    }

    override fun getItemCount(): Int {

        return question.answers.size + HEADER_ITEM_COUNT
    }


    private fun displayImageFromFirebaseStorage(imageUrl: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                // Sets the mathView text for the question detail and disables zooming on the view
                holder.headerText.setDisplayText(question.questionText)
                holder.headerText.settings.displayZoomControls = false

                if (question!!.imageURI == "") {
                    holder.image.visibility = View.GONE
                } else {
                    holder.image.visibility = View.VISIBLE
                    displayImageFromFirebaseStorage(question!!.imageURI, holder.image)
                }

            }

            is AnswerViewHolder -> {
                //holder.answerText.text = currentAnswerItem
                val answerId = question.answers[position-1]
                val futureAnswer = db.getAnswerById(answerId)
                val futureLikeList = db.getAnswerLike(answerId)
                CompletableFuture.allOf(futureAnswer, futureLikeList).thenAccept {

                    val currentAnswerItem = futureAnswer.get() ?: Model.Answer()
                    val endorsementList = futureLikeList.get()
                    db.getUserById(currentAnswerItem.userId).thenAccept {
                        if(!question.isAnonymous) {
                            holder.username.text = it?.username
                        } else {
                            if(anonymouseNameMap.contains(it?.userId)) {
                                holder.username.text = anonymouseNameMap[it?.userId]
                            } else {
                                if(anonymouseNameMap.size < DatabaseManager.anonymousUsers.size){
                                    var leftAnonymousNames = DatabaseManager.anonymousUsers.toMutableList()
                                    leftAnonymousNames.removeAll(anonymouseNameMap.values)
                                    val name = leftAnonymousNames[Random.nextInt(0, leftAnonymousNames.size)]

                                    anonymouseNameMap[it?.userId!!] = name
                                    holder.username.text = name
                                } else {
                                    holder.username.text = mainActivity.getString(R.string.anonymous)
                                }
                            }
                        }
                    }

                    // Sets the mathView text for the answer and disables zooming on the view
                    holder.answerText.setDisplayText(currentAnswerItem.answerText)
                    holder.answerText.settings.displayZoomControls = false

                    holder.button.setOnClickListener{
                        db.addChatsWith(DatabaseManager.user!!.userId, currentAnswerItem.userId)
                        val intent = Intent(
                            mainActivity,
                            MainActivity::class.java
                        )
                        intent.putExtra("fragment", "RealChat")
                        intent.putExtra("externID", currentAnswerItem.userId)
                        startActivity(mainActivity,intent,null)
                    }

                    val like = holder.itemView.findViewById<ImageButton>(R.id.likeButton)
                    val counter = holder.itemView.findViewById<TextView>(R.id.likeCount)
                    val endorsementCount = endorsementList.size
                    val isEndorsed = endorsementList.contains(DatabaseManager.user?.userId)
                    counter.text = (endorsementCount).toString()
                    if(isEndorsed) {
                        like.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.red), PorterDuff.Mode.SRC_IN)
                    } else {
                        like.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_gray), PorterDuff.Mode.SRC_IN)
                    }

                    like.tag = listOf(isEndorsed, endorsementCount)

                    like.setOnClickListener {
                        val userId = user?.userId
                        if (userId != null) {
                            val tags = like.tag as List<*>
                            val isEndorsed = tags[0] as Boolean
                            val count = tags[1] as Int
                            if (!isEndorsed) {
                                db.addAnswerLike(userId, currentAnswerItem.answerId)
                                //turn like color to red ans increment counter
                                like.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.red), PorterDuff.Mode.SRC_IN)
                                counter.setText((count + 1).toString())
                                it.tag = listOf(true, count + 1)
                            } else {
                                db.removeAnswerLike(userId, currentAnswerItem.answerId)
                                // turn like color to light gray and decrement counter
                                like.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_gray), PorterDuff.Mode.SRC_IN)
                                counter.setText((count - 1).toString())
                                it.tag = listOf(false, count - 1)
                            }
                        }
                    }
                }

                val userId = user?.userId
                var futureUserStatus = CompletableFuture.completedFuture<UserStatus?>(null)
                val futureEndorsement = db.getAnswerEndorsement(answerId)
                if(userId != null) {
                    futureUserStatus = db.getUserStatus(userId, question.courseId)
                }

                CompletableFuture.allOf(futureEndorsement, futureUserStatus).thenAccept {
                    val endorserName = futureEndorsement.get()
                    val userStatus = futureUserStatus.get()

                    //available for everyone
                    val endorsementText = holder.itemView.findViewById<TextView>(R.id.endorsementText)
                    val endorsementButton = holder.itemView.findViewById<ImageButton>(R.id.endorsementButton)

                    if(endorserName != null  && endorserName.isNotEmpty()){
                        endorsementText.text = "Approved by "+endorserName+"."
                        endorsementText.visibility = VISIBLE
                        endorsementButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_blue), PorterDuff.Mode.SRC_IN)
                    } else {
                        endorsementButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_gray), PorterDuff.Mode.SRC_IN)
                    }

                    //only for privileged USER
                    if(userStatus != null){
                        //set up the endorsement text and button
                        endorsementButton.visibility = VISIBLE
                        endorsementButton.tag = (endorserName != null && endorserName!="")

                        //set the button listener
                        endorsementButton.setOnClickListener {
                            if(!(it.tag as Boolean)){
                                val name = user?.username ?: "someone"
                                db.addAnswerEndorsement(answerId, name)
                                endorsementText.text = "Approved by "+name+"."
                                endorsementText.visibility = VISIBLE
                                endorsementButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_blue), PorterDuff.Mode.SRC_IN)
                                it.tag = true
                            } else {
                                db.removeAnswerEndorsement(answerId)
                                endorsementText.visibility = GONE
                                endorsementButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_gray), PorterDuff.Mode.SRC_IN)
                                it.tag = false
                            }
                        }
                    }



                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == HEADER_ITEM_POSITION) {
            return HEADER_ITEM_TYPE
        }

        return CLASSIC_ITEM_TYPE
    }


    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerText : MathView = itemView.findViewById(R.id.qdetails_question_content)
        val image : ImageView = itemView.findViewById(R.id.image_question)
    }

    class AnswerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val username : TextView = itemView.findViewById(R.id.qdetails_answer_username)
        val answerText : MathView = itemView.findViewById(R.id.qdetails_answer_text)
        val button : ImageButton = itemView.findViewById(R.id.chatWithUser)
    }

}

