package com.github.ybecker.epforuml

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.cache.MockSavedQuestionsCache
import com.github.ybecker.epforuml.cache.SavedQuestionsCache
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


/**
 * A simple [Fragment] subclass.
 * Use the [SavedQuestionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SavedQuestionsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var user : Model.User

    private var savedQuestions : SavedQuestionsCache? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_saved_questions, container, false)

        savedQuestions = savedInstanceState!!.getParcelable("savedQuestions")

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize recycler with saved questions
        user = DatabaseManager.user ?: Model.User()
        val userId = user.userId


        val list = savedQuestions?.toList()

        if (userId.isNotEmpty() && list != null && list.isNotEmpty()) {
            val layoutManager = LinearLayoutManager(context)
            recyclerView = view.findViewById(R.id.recycler_forum)
            recyclerView.layoutManager = layoutManager

            // display saved questions
            val adapter = ForumAdapter(list)
            recyclerView.adapter = adapter

            // move to QuestionDetails when clicking on specific question
            adapter.onItemClick = {q ->
                val intent = Intent(this.context, QuestionDetailsActivity::class.java)
                intent.putExtra("question", q)
                startActivity(intent)
            }
            // if no question to display
        } else {
            val questions : RecyclerView = view.findViewById(R.id.recycler_saved_questions)
            questions.visibility = View.GONE

            val text : TextView = view.findViewById(R.id.text_login_to_save)
            text.visibility = View.VISIBLE

            // user is logged in but no questions to display
            if (userId.isNotEmpty()) {
                text.text = "No saved questions."
            }
        }

    }
}