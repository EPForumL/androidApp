package com.github.ybecker.epforuml.features.chat

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.ybecker.epforuml.util.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.DatabaseManager.user


/**
 * This activity represents a search view taking a list and treating the results as asked
 * In the future, I would like to make Generic so that it can be reused with other lists
 */
class SearchActivity : AppCompatActivity() {

    private lateinit var list: ArrayList<String>

    private lateinit var listView: ListView
    private lateinit var listAdapter: ArrayAdapter<String>
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        listView = findViewById(R.id.listView)
        searchView = findViewById(R.id.searchView)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        db.registeredUsers().thenAccept { listIt ->
            list = listIt as ArrayList<String>

            listAdapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                list
            )
            listView.adapter = this.listAdapter

            listView.setOnItemClickListener { _, _, position, _ ->
                db.getUserId(listAdapter.getItem(position)!!).thenAccept {
                    db.addChatsWith(user!!.userId, it)
                    val intent = Intent(
                        this,
                        MainActivity::class.java
                    )
                    intent.putExtra("fragment", "RealChat")
                    intent.putExtra("externID", it)
                    startActivity(intent)
                }


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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragment", "chatHome")
            startActivity(intent)
            finish()
        }

        return true
    }
}

