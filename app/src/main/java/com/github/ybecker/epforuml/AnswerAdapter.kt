package com.github.ybecker.epforuml

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AnswerAdapter(private val questionText : String, private val answerList : MutableList<String>)
    : RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.question_details_answer_item, parent, false)
        return AnswerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return answerList.size
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val currentItem = answerList[position]
        holder.currentText.text = currentItem
    }

    class AnswerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val currentText : TextView = itemView.findViewById(R.id.qdetails_answer_text)
    }
}

/*
class ForumAdapter(private val questionsList : MutableList<Model.Question>) :
    RecyclerView.Adapter<ForumAdapter.ForumViewHolder>() {

    var onItemClick :((Model.Question) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.forum_item, parent, false)
        return ForumViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return questionsList.size
    }

    override fun onBindViewHolder(holder: ForumViewHolder, position: Int) {
        val currentItem = questionsList[position]
        holder.currentText.text = currentItem.questionTitle

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }

    class ForumViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val currentText : TextView = itemView.findViewById(R.id.forum_question_displayed)
    }
}
 */