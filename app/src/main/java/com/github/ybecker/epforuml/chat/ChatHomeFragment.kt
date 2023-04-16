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
import com.github.ybecker.epforuml.database.DatabaseManager.user

/**
 * A fragment representing a list of Items.
 */
class ChatHomeFragment(private val mainActivity: MainActivity) : Fragment() {

    private lateinit var chatList: List<String>
    private lateinit var chatHomeAdapter: ChatHomeAdapter
    private lateinit var chatHomeRecyclerView: RecyclerView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layout.fragment_chat_home_list, container, false)
        if (user == null) {
            val notConnected = view?.findViewById<TextView>(R.id.not_connected_text_view)
            notConnected?.visibility = View.VISIBLE
        } else {
            chatList = user!!.chatsWith
        }
        view.findViewById<Button>(R.id.newChatWith).setOnClickListener{
            val switchActivityIntent = Intent(this.mainActivity, SearchActivity::class.java)
            startActivity(switchActivityIntent)
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
        // Update questions list
        if(user!=null) {
            fetchChats()
        }
    }
    private fun fetchChats() {
        if(user!!.chatsWith.isNotEmpty()) {
            chatHomeAdapter = ChatHomeAdapter(user!!.chatsWith as MutableList<String>,this.mainActivity)
            chatHomeRecyclerView.adapter = chatHomeAdapter
        }else{
            val noChats = view?.findViewById<TextView>(R.id.no_chats)
            noChats?.visibility = View.VISIBLE
        }

    }
}