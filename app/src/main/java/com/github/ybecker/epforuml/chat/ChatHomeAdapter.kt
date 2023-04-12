package com.github.ybecker.epforuml.chat

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model

class ChatHomeAdapter(private val chatList : MutableList<String>,private val mainActivity: MainActivity) :
    RecyclerView.Adapter<ChatHomeAdapter.ChatHomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHomeViewHolder {
        val itemViewHost = LayoutInflater.from(parent.context).inflate(R.layout.chat_home_item, parent, false)
        return ChatHomeViewHolder(itemViewHost)
    }


    override fun onBindViewHolder(holder: ChatHomeViewHolder, position: Int) {
        val currentItem = chatList[position]
        val user : Model.User? = DatabaseManager.db.getUserById(currentItem).get()
        holder.chatWithButton.text = "Chat with " + user?.username
        if(user?.profilePic!="") holder.chatImage.setImageURI(Uri.parse(user?.profilePic))
        holder.chatWithButton.setOnClickListener{


            mainActivity.intent.putExtra("externID", currentItem)
            mainActivity.replaceFragment(RealChatFragment())
        }

    }
    class ChatHomeViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val chatImage : ImageView = itemView.findViewById(R.id.profilePicture)
        val chatWithButton : Button = itemView.findViewById(R.id.buttonChatWith)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}