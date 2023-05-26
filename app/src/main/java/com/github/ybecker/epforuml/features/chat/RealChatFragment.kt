package com.github.ybecker.epforuml.features.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.util.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.R.*
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.DatabaseManager.user
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CompletableFuture

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layout.fragment_chat_list, container, false)
        val button = view.findViewById<ImageButton>(R.id.send_text)
        val textMsg = view.findViewById<EditText>(R.id.edit_text_message)
        noChats = view?.findViewById(R.id.no_chats)!!
        notConnected = view.findViewById(R.id.not_connected_text_view)!!

        val buttonHome: ImageButton = view.findViewById(R.id.back_to_home_button)
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
                    val button = view.findViewById<ImageButton>(R.id.send_text)
                    button?.visibility = View.VISIBLE
                    button.setOnClickListener{
                        val chat = db.addChat(hostId, externId,textMsg.text.toString())
                        queryList.add(chat)
                        textMsg.setText("")
                        displayChats()
                    }
                }else{
                    val notFound = view.findViewById<TextView>(R.id.not_found)
                    notFound?.visibility = View.VISIBLE
                }
            }
        }
        return view
    }

    /**
     * Retrieves the messages of a specific user upon connection to the chat session
     * @param textMsg the text box
     * @param button the send button
     * @param view the current view
     */
    private fun retrieveUsersInitialMessages(
        textMsg: EditText,
        button: ImageButton,
        view: View
    ) {
        notConnected.visibility = View.GONE
        textMsg?.visibility = View.VISIBLE
        button?.visibility = View.VISIBLE
        hostId = user!!.userId
        externId = this.activity?.intent?.getStringExtra("externID").toString()
        hostUser = user!!
        db.getUserById(externId).thenAccept {
            if (it != null) {
                externUser = it
                chatList = db.getChat(hostId, externId)
                view.findViewById<TextView>(R.id.title_chat).text = externUser.username
                val button = view.findViewById<ImageButton>(R.id.send_text)
                button?.visibility = View.VISIBLE
                button.setOnClickListener {
                    val chat = db.addChat(hostId, externId, textMsg.text.toString())
                    queryList.add(chat)
                    textMsg.setText("")
                    displayChats()
                }
            } else {
                val notFound = view?.findViewById<TextView>(R.id.not_found)
                notFound?.visibility = View.VISIBLE
            }
        }
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
        //startTimer()
        setUpDbListener()
    }

    /**
     * Sets up a listener to refresh the chat upon changes in the database
     */
    private fun setUpDbListener() {
        if(db.getDbInstance()!=null){
            val database = db.getDbInstance()!!.reference
            val docRef: DatabaseReference = database.child("chats")
            docRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    fetchChats()
                }
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    fetchChats()
                }
                override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })}



    }

    /**
     * This function fetches the chats from the Firebase DB.
     * It displays a message in special cases.
     */
    fun fetchChats() {
        var chats : ArrayList<Model.Chat> = arrayListOf()
        db.getChat(hostId,externId).thenAccept{
            chats = it as ArrayList<Model.Chat>
            if(chats.isNotEmpty()) {
                queryList =  chats
                displayChats()
            }
        }

    }

    /**
     * This function displays the chats between two people.
     */
    private fun displayChats() {
        if (queryList.isEmpty()) {
            noChats.visibility = View.VISIBLE
        } else {
            noChats.visibility = View.GONE
            queryList.sortBy { LocalDateTime.parse(it.date) }
            chatAdapter = ChatAdapter(queryList, externUser, this.activity as MainActivity, this)
            chatRecyclerView.adapter = chatAdapter
        }
    }
}
