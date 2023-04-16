package com.github.ybecker.epforuml.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SearchView.OnQueryTextListener
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.R.*
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.DatabaseManager.user
import com.github.ybecker.epforuml.database.Model
import com.github.ybecker.epforuml.databinding.ActivityMainBinding

/**
 * A fragment representing a list of Items.
 */
class ChatHomeFragment(private val mainActivity: MainActivity) : Fragment() {

    private lateinit var chatList: List<String>
    private lateinit var chatHomeAdapter: ChatHomeAdapter
    private lateinit var chatHomeRecyclerView: RecyclerView
    private lateinit var adapter: ArrayAdapter<Model.User>
    private lateinit var usersList : List<Model.User>



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

        usersList = db.registeredUsers().get();
        setupListView(view)
        setupSearchView(view)
        return view
    }

    private fun setupListView(view: View) {
        adapter = ArrayAdapter(this.requireContext(), android.R.layout.simple_list_item_1, usersList)
        view.findViewById<ListView>(R.id.listView).adapter = adapter
    }

    private fun setupSearchView(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val isMatchFound = usersList.filter { user -> user.username.contains(p0!!) }
                if (isMatchFound.size == 1) {
                    //create new chat with this person
                    db.addChatsWith(user!!.userId, isMatchFound[0].userId)

                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                return false
            }
        })

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
            updateChats()
        }
    }

    private fun updateChats() {
        fetchChats()
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