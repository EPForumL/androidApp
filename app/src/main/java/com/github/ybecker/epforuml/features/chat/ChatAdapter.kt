package com.github.ybecker.epforuml.features.chat

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.util.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.basicEntities.account.AccountFragment
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model

/**
 * This class is an adapter for the Chat Fragment
 * @param chatList represents the list of chats between the logged user and chosen pal
 * @param externUser the User the host is chatting with
 * It will create a recycler view, treating each chat correctly and outputting the correct view
 */
class ChatAdapter(var chatList : MutableList<Model.Chat>, var externUser : Model.User, private val mainActivity: MainActivity, private val fragment : RealChatFragment) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemViewHost = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(itemViewHost)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        db.getUserById(DatabaseManager.user!!.userId).thenAccept{
            val hostUser = it!!
            val currentItem = chatList[position]
            if(currentItem.senderId == hostUser.userId){
                holder.currentText.text = currentItem.text

                // Load profile picture to image view
                AccountFragment.loadProfilePictureToView(
                    holder.itemView.context,
                    hostUser.profilePic,
                    holder.chatImage
                )

                holder.itemView.setOnLongClickListener {
                    onLongClickListener(currentItem)
                }

            }else{
                holder.currentText.text = currentItem.text

                // Load profile picture to image view
                AccountFragment.loadProfilePictureToView(
                    holder.itemView.context,
                    externUser.profilePic,
                    holder.chatImage
                )

                holder.itemView.scaleX = -1f
                holder.itemView.findViewById<TextView>(R.id.textChat).scaleX = -1f

            }
        }
    }

    /**
     * Sets the button to delete the chat on long click
     * @param currentItem the chat item to be deleted
     * @return A boolean to assert if the chat was successfully deleted
     */
    private fun onLongClickListener(currentItem: Model.Chat): Boolean {
        val alertDialogBuilder = AlertDialog.Builder(mainActivity)
        alertDialogBuilder.setTitle("Do you want to delete this message?")
        alertDialogBuilder.setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
            db.removeChat(currentItem.chatId!!)
            mainActivity.intent.putExtra("externID", currentItem.receiverId)
            mainActivity.replaceFragment(RealChatFragment())
        }
        alertDialogBuilder.setNegativeButton(android.R.string.cancel, null)
        alertDialogBuilder.show()
        return true
    }


    class ChatViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val currentText : TextView = itemView.findViewById(R.id.textChat)
        val chatImage : ImageView = itemView.findViewById(R.id.profilePicture)
    }
}