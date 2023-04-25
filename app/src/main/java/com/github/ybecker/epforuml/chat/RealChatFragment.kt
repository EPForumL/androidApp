package com.github.ybecker.epforuml.chat

import android.content.Intent
import android.opengl.Visibility
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
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.R.*
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.DatabaseManager.user
import com.github.ybecker.epforuml.database.Model
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.ArrayList

/**
 * A fragment representing a chat which is a list of messages.
 */
class RealChatFragment : Fragment() {

    private lateinit var chatList: CompletableFuture<List<Model.Chat>>
    private var queryList = mutableListOf<Model.Chat>()
    private lateinit var hostId:String
    private lateinit var externId: String
    private lateinit var noChats : TextView
    private lateinit var notConnected : TextView


    private lateinit var hostUser :Model.User
    private lateinit var externUser : Model.User

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var timer: Timer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layout.fragment_chat_list, container, false)
        val button = view.findViewById<Button>(R.id.send_text)
        val textMsg = view.findViewById<EditText>(R.id.edit_text_message)
        noChats = view?.findViewById(R.id.no_chats)!!
        notConnected = view?.findViewById(R.id.not_connected_text_view)!!

        val buttonHome: Button = view.findViewById(R.id.back_to_home_button)
        buttonHome.setOnClickListener { // Create an intent to return to the previous fragment
            val intent = Intent(this.context, MainActivity::class.java)
            intent.putExtra("fragment", "chatHome")
            startActivity(intent)
        }

        if (user == null) {
            notConnected.visibility = View.VISIBLE
            textMsg?.visibility = View.GONE
            button?.visibility = View.GONE

        } else {
            notConnected.visibility = View.GONE
            textMsg?.visibility = View.VISIBLE
            button?.visibility = View.VISIBLE
            hostId = user!!.userId
            externId = this.activity?.intent?.getStringExtra("externID").toString()
            hostUser = user!!
            db.getUserById(externId).thenAccept{
                if (it != null) {
                    externUser = it
                    chatList = db.getChat(hostId, externId)
                    view.findViewById<TextView>(R.id.title_chat).text = externUser.username
                    val button = view.findViewById<Button>(R.id.send_text)
                    button?.visibility = View.VISIBLE
                    button.setOnClickListener{
                        val chat = db.addChat(hostId, externId,textMsg.text.toString())
                        queryList.add(chat)
                        displayChats()
                    }
                }else{
                    val notFound = view?.findViewById<TextView>(R.id.not_found)
                    notFound?.visibility = View.VISIBLE
                }
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
        if (user != null) fetchChats()
        startTimer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
    }

    private fun startTimer() {
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // Code to refresh the fragment goes here
                activity?.runOnUiThread {
                    // Update the UI
                    fetchChats()
                }
            }
        }, 0, 2000)
    }

    private fun stopTimer() {
        timer.cancel()
    }


    private fun fetchChats() {
        var chats : ArrayList<Model.Chat> = arrayListOf()
        db.getChat(hostId,externId).thenAccept{
            chats = it as ArrayList<Model.Chat>
            if(chats.isNotEmpty()) {
                queryList =  chats
                displayChats()
            }
        }

    }

    private fun displayChats() {
        if (queryList.isEmpty()) {
            noChats.visibility = View.VISIBLE
        } else {
            noChats.visibility = View.GONE
            queryList.sortBy { LocalDateTime.parse(it.date) }
            chatAdapter = ChatAdapter(queryList, externUser, this as MainActivity)
            chatRecyclerView.adapter = chatAdapter
        }
    }
}