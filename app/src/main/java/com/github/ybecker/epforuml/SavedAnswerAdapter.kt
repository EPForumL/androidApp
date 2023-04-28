package com.github.ybecker.epforuml

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import java.util.concurrent.CompletableFuture

class SavedAnswerAdapter(private val questionId : String, private val questionText : String, private val answerList : List<Model.Answer>, private val mainActivity: Activity)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val HEADER_ITEM_TYPE = 0
        private const val CLASSIC_ITEM_TYPE = 1

        private const val HEADER_ITEM_COUNT = 1
        private const val HEADER_ITEM_POSITION = 0
    }

    private val questionAnswers = answerList.filter{
        it.questionId == questionId
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
        return answerList.size + HEADER_ITEM_COUNT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.headerText.text = questionText
            }

            is AnswerViewHolder -> {

                val currentAnswerItem = answerList[position-1]

                holder.answerText.text = currentAnswerItem.answerText

                // TODO : change userId to username (need to use future)
                holder.username.text = currentAnswerItem.userId

                val counter = holder.itemView.findViewById<TextView>(R.id.likeCount)
                val endorsementCount = currentAnswerItem.endorsements.size
                counter.text = (endorsementCount).toString()

                val like = holder.itemView.findViewById<ImageButton>(R.id.likeButton)
                like.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_gray), PorterDuff.Mode.SRC_IN)

                //holder.answerText.text = currentAnswerItem
                /*
                val answerId = answerList.get(position-1)
                val futureAnswer = db.getAnswerById(answerId)
                val futureEndorsementList = db.getAnswerEndorsements(answerId)
                CompletableFuture.allOf(futureAnswer, futureEndorsementList).thenAccept {



                    val currentAnswerItem = futureAnswer.get() ?: Model.Answer()
                    val endorsementList = futureEndorsementList.get()

                    holder.answerText.text = currentAnswerItem.answerText
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
                    // TODO : change userId to username (need to use future)
                    holder.username.text = currentAnswerItem.userId

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
                        val userId = DatabaseManager.user?.userId
                        if (userId != null) {
                            val tags = like.tag as List<*>
                            val isEndorsed = tags[0] as Boolean
                            val count = tags[1] as Int
                            if (!isEndorsed) {
                                db.addAnswerEndorsement(userId, currentAnswerItem.answerId)
                                //turn like color to red ans increment counter
                                like.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.red), PorterDuff.Mode.SRC_IN)
                                counter.setText((count + 1).toString())
                                it.tag = listOf(true, count + 1)
                            } else {
                                db.removeAnswerEndorsement(userId, currentAnswerItem.answerId)
                                // turn like color to light gray and decrement counter
                                like.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_gray), PorterDuff.Mode.SRC_IN)
                                counter.setText((count - 1).toString())
                                it.tag = listOf(false, count - 1)
                            }
                        }
                    }
                }

                 */
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
        val headerText : TextView = itemView.findViewById(R.id.qdetails_question_content)
    }

    class AnswerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val username : TextView = itemView.findViewById(R.id.qdetails_answer_username)
        val answerText : TextView = itemView.findViewById(R.id.qdetails_answer_text)
        val button : Button = itemView.findViewById(R.id.chatWithUser)
    }

}

