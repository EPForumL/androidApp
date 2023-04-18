package com.github.ybecker.epforuml.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.R.*
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.DatabaseManager.user
import java.util.concurrent.TimeUnit

/**
 * A fragment representing a list of Chats.
 */
class ChatHomeFragment : Fragment() {

    private lateinit var chatList: List<String>
    private lateinit var chatHomeAdapter: ChatHomeAdapter
    private lateinit var chatHomeRecyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layout.fragment_chat_home_list, container, false)
        val newChatButton = view.findViewById<Button>(R.id.newChatWith)
        if (user == null) {
            val notConnected = view?.findViewById<TextView>(R.id.not_connected_text_view)
            notConnected?.visibility = View.VISIBLE
            newChatButton.visibility = View.INVISIBLE
        } else {
            chatList = user!!.chatsWith
        }
        return view
    }


    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        // Configure recycler view and adapter
        val linearLayoutMgr = LinearLayoutManager(context)
        chatHomeRecyclerView = fragmentView.findViewById(R.id.recycler_chat_home)
        chatHomeRecyclerView.layoutManager = linearLayoutMgr
        chatHomeRecyclerView.setHasFixedSize(false)
        val newChatButton = view?.findViewById<Button>(R.id.newChatWith)
        newChatButton?.setOnClickListener(listener)
        // Update chats list
        if (user != null) {
            fetchChats()
        }
    }

    private fun fetchChats() {
        if (user!!.chatsWith.isNotEmpty()) {
            chatHomeAdapter = ChatHomeAdapter(
                user!!.chatsWith as MutableList<String>,
                this.activity as MainActivity
            )
            chatHomeRecyclerView.adapter = chatHomeAdapter
        } else {
            val noChats = view?.findViewById<TextView>(R.id.no_chats)
            noChats?.visibility = View.VISIBLE
        }


    }

    private val listener: View.OnClickListener? = View.OnClickListener {
        val switchActivityIntent = Intent(this.activity, SearchActivity::class.java)
        startActivity(switchActivityIntent)


    }
}