package com.github.ybecker.epforuml.chat

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager.db


class SearchActivity : AppCompatActivity() {

    lateinit var listView: ListView
    lateinit var listAdapter: ArrayAdapter<String>
    lateinit var list: ArrayList<String>;
    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        listView = findViewById(R.id.listView)
        searchView = findViewById(R.id.searchView)
        list = db.registeredUsers().get() as ArrayList<String>

        listAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            list
        )
        listView.adapter = listAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (list.contains(query)) {
                    listAdapter.filter.filter(query)
                } else {
                    Toast.makeText(this@SearchActivity, "No results found..", Toast.LENGTH_LONG)
                        .show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                listAdapter.filter.filter(newText)
                return false
            }
        })
    }
}

/*
class SearchActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<Model.User>
    private lateinit var usersList : List<Model.User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        usersList = DatabaseManager.db.registeredUsers().get();
        setupListView()
        setupSearchView()
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
                    DatabaseManager.db.addChatsWith(DatabaseManager.user!!.userId, isMatchFound[0].userId)
                    mainActivity.intent.putExtra("externID", isMatchFound[0].userId)
                    mainActivity.replaceFragment(RealChatFragment())
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                return false
            }
        })

    }
}*/
