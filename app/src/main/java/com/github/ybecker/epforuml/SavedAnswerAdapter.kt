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
import com.github.ybecker.epforuml.latex.MathView
//import katex.hourglass.`in`.mathlib.MathView


import java.util.concurrent.CompletableFuture
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

class SavedAnswerAdapter(private val question: Model.Question,
                         private val answerList : List<Model.Answer>,
                         private val userList : List<Model.User>,
                        private val anonymousUsernameMap : HashMap<String, String>,
                         private val mainActivity: Activity)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val HEADER_ITEM_TYPE = 0
        private const val CLASSIC_ITEM_TYPE = 1

        private const val HEADER_ITEM_COUNT = 1
        private const val HEADER_ITEM_POSITION = 0
    }

    private val questionAnswers = answerList.filter{
        it.questionId == question.questionId
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
        return questionAnswers.size + HEADER_ITEM_COUNT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.headerText.setDisplayText(question.questionText)
            }

            is AnswerViewHolder -> {

                val currentAnswerItem = questionAnswers[position-1]

                holder.answerText.setDisplayText(currentAnswerItem.answerText)

                holder.username.text = currentAnswerItem.userId

                val like = holder.itemView.findViewById<ImageButton>(R.id.likeButton)
                like.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_gray), PorterDuff.Mode.SRC_IN)

                var answerUserUsername : String? = null
                val list = userList.filter { it.userId == currentAnswerItem.userId }
                if (list.isNotEmpty()) answerUserUsername = list[0].username

                //if the question is not anonymous write the real username
                if(!question.isAnonymous) {
                    holder.username.text = answerUserUsername
                } else {
                    holder.username.text = "Anonymous user"
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
    }

    class AnswerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val username : TextView = itemView.findViewById(R.id.qdetails_answer_username)
        val answerText : MathView = itemView.findViewById(R.id.qdetails_answer_text)
    }

}

