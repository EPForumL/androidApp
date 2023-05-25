package com.github.ybecker.epforuml

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.ybecker.epforuml.account.AccountFragment
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import com.google.android.material.imageview.ShapeableImageView

// Adapter for the forum questions recycler view
class ForumAdapter(private val questionsList : MutableList<Model.Question>) :
    RecyclerView.Adapter<ForumAdapter.ForumViewHolder>() {

    // Click listener for the forum questions
    var onItemClick :((Model.Question) -> Unit)? = null

    // Inflate the forum item layout and create a new view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.forum_item, parent, false)
        return ForumViewHolder(itemView)
    }

    // Return the number of items in the list
    override fun getItemCount(): Int {
        return questionsList.size
    }

    // Bind the data to the view holder
    override fun onBindViewHolder(holder: ForumViewHolder, position: Int) {
        val currentItem = questionsList[position]

        // Load profile picture to image view
        db.getUserById(currentItem.userId).thenAccept {
            if (it != null) {
                AccountFragment.loadProfilePictureToView(
                    holder.itemView.context,
                    it.profilePic,
                    holder.profilePicture
                )
            }
        }

        // Set the text of the question title to the view holder
        holder.currentText.text = currentItem.questionTitle

        // Set the click listener for the item view
        holder.itemView.setOnClickListener {
            // Invoke the onItemClick callback when the item is clicked
            onItemClick?.invoke(currentItem)
        }
    }

    // View holder for the forum questions
    class ForumViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val profilePicture : ImageView = itemView.findViewById(R.id.question_image)
        val currentText : TextView = itemView.findViewById(R.id.forum_question_displayed)
    }
}