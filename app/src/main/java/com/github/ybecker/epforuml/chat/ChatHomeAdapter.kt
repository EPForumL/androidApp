package com.github.ybecker.epforuml.chat

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.account.AccountFragment
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model

/**
 * This class is an adapter for the Chat Fragment
 * @param chatList representents the list of chats between the logged user and chosen pal
 * @param mainActivity the parent activity of the fragment calling the adapter
 * It will create a recycler view, treating each chat correctly and outputing the correct view
 */
class ChatHomeAdapter(private val chatList : MutableList<String>,private val mainActivity: MainActivity) :
    RecyclerView.Adapter<ChatHomeAdapter.ChatHomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHomeViewHolder {
        val itemViewHost = LayoutInflater.from(parent.context).inflate(R.layout.chat_home_item, parent, false)
        return ChatHomeViewHolder(itemViewHost)
    }


    override fun onBindViewHolder(holder: ChatHomeViewHolder, position: Int) {
        val currentItem = chatList[position]
         DatabaseManager.db.getUserById(currentItem).thenAccept{
             val user : Model.User? = it
             holder.chatWithText.text = user?.username

             // Load profile picture to image view
             if (user != null) {
                 AccountFragment.loadProfilePictureToView(
                     holder.itemView.context,
                     user.profilePic,
                     holder.chatImage
                 )
             }

             holder.chatWithButton.setOnClickListener{
                 mainActivity.intent.putExtra("externID", currentItem)
                 mainActivity.replaceFragment(RealChatFragment())
             }
         }
    }
    class ChatHomeViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val chatImage : ImageView = itemView.findViewById(R.id.profilePicture)
        val chatWithText : TextView = itemView.findViewById(R.id.whoToChatWith)
        val chatWithButton : CardView = itemView.findViewById(R.id.buttonChatWith)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}