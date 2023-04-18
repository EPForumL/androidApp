package com.github.ybecker.epforuml.chat

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.DatabaseManager.user
import java.util.Objects


/**
 * This activity represents a search view taking a list and treating the results as asked
 * In the future, I would like to make Generic so that it can be reused with other lists
 */
class SearchActivity : AppCompatActivity() {

    private lateinit var list: ArrayList<String>

    private lateinit var listView: ListView
    private lateinit var listAdapter: ArrayAdapter<String>
    private lateinit var searchView: SearchView
    val searchActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        listView = findViewById(R.id.listView)
        searchView = findViewById(R.id.searchView)

        val button: Button = findViewById(R.id.back_to_home_button)
        button.setOnClickListener { // Create an intent to return to the previous fragment
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("fragment", "chatHome")
            startActivity(intent)
        }

        val futureList = db.registeredUsers()
        futureList.thenAccept {
            list = futureList.get() as ArrayList<String>

            listAdapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                list
            )
            listView.adapter = listAdapter

            listView.setOnItemClickListener { _, _, position, _ ->
                var id = ""
                db.getUserId(listAdapter.getItem(position)!!).thenAccept {
                    id = it
                }
                db.addChatsWith(user!!.userId, id)
                val intent = Intent(
                    searchActivity,
                    MainActivity::class.java
                )
                intent.putExtra("fragment", "RealChat")
                intent.putExtra("externID", id)
                startActivity(intent)

            }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (list.any { s -> s.contains(query!!) }) {
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
}

