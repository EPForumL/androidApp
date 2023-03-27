package com.github.ybecker.epforuml

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model

class AnswerAdapter(private val questionId : String, private val questionText : String, private val answerList : List<String>)
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
        return answerList.size + HEADER_ITEM_COUNT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.headerText.text = questionText
            }

            is AnswerViewHolder -> {
                var currentAnswerItem : Model.Answer //= answerList[position-1]
                //holder.answerText.text = currentAnswerItem

                db.getQuestionAnswers(questionId).thenAccept {
                    currentAnswerItem = it[position-1]

                    holder.answerText.text = currentAnswerItem.answerText
                    holder.username.text = currentAnswerItem.userId
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
        val headerText : TextView = itemView.findViewById(R.id.qdetails_question_content)
    }

    class AnswerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val username : TextView = itemView.findViewById(R.id.qdetails_answer_username)
        val answerText : TextView = itemView.findViewById(R.id.qdetails_answer_text)
    }


}

