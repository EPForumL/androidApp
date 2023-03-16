package com.github.ybecker.epforuml

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.android.material.imageview.ShapeableImageView

class ForumAdapter(private val questionsList : MutableList<Model.Question>) :
    RecyclerView.Adapter<ForumAdapter.ForumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.forum_item, parent, false)
        return ForumViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return questionsList.size
    }

    override fun onBindViewHolder(holder: ForumViewHolder, position: Int) {
        val currentItem = questionsList[position]
        holder.currentText.text = currentItem.questionText
    }

    class ForumViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val currentText : TextView = itemView.findViewById(R.id.forum_question_displayed)
    }


}