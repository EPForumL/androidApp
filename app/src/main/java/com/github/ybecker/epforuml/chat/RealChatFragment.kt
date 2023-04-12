package com.github.ybecker.epforuml.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.R.*
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.DatabaseManager.user
import com.github.ybecker.epforuml.database.Model
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

/**
 * A fragment representing a list of Items.
 */
class RealChatFragment : Fragment() {

    private lateinit var chatList: CompletableFuture<List<Model.Chat>>
    private var queryList = mutableListOf<Model.Chat>()
    private lateinit var hostId:String
    private lateinit var externId: String

    private lateinit var hostUser :Model.User
    private lateinit var externUser : Model.User

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layout.fragment_chat_list, container, false)
        val button = view.findViewById<Button>(R.id.send_text)
        val textMsg = view.findViewById<EditText>(R.id.edit_text_message)
        if (user == null) {
            val notConnected = view?.findViewById<TextView>(R.id.not_connected_text_view)
            notConnected?.visibility = View.VISIBLE
            textMsg?.visibility = View.INVISIBLE
            button?.visibility = View.INVISIBLE
        } else {
            hostId = user!!.userId
            externId = this.activity?.intent?.getStringExtra("externID").toString()
            hostUser = user!!
            externUser = db.getUserById(externId).get()!!
            chatList = db.getChat(hostId, externId)
            view.findViewById<TextView>(R.id.title_chat).text = externUser.username
            val button = view.findViewById<Button>(R.id.send_text)
            button?.visibility = View.VISIBLE
            button.setOnClickListener{
                db.addChat(hostId, externId,textMsg.text.toString())
                updateChats()
            }
        }


        return view
    }

    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)

        // Configure recycler view and adapter
        val linearLayoutMgr = LinearLayoutManager(context)
        chatRecyclerView = fragmentView.findViewById(R.id.recycler_chat)
        chatRecyclerView.layoutManager = linearLayoutMgr
        chatRecyclerView.setHasFixedSize(false)
        // Update questions list
        if(user!=null) updateChats()
    }

    private fun updateChats() {
        fetchChats()
    }
    private fun fetchChats() {
        if(db.getChat(hostId,externId).get().isNotEmpty())
            queryList = db.getChat(hostId,externId).get() as MutableList<Model.Chat>
        displayChats()

    }

    private fun displayChats() {
        if (queryList.isEmpty()) {
            val noChats = view?.findViewById<TextView>(R.id.no_chats)
            noChats?.visibility = View.VISIBLE
        } else {
            queryList.sortBy { LocalDateTime.parse(it.date) }
            chatAdapter = ChatAdapter(queryList,hostUser, externUser)
            chatRecyclerView.adapter = chatAdapter
        }
    }
}