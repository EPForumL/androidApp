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
import com.github.ybecker.epforuml.database.Model


/**
 * A simple [Fragment] subclass.
 * Use the [SavedQuestionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SavedQuestionsFragment : Fragment() {

    private var savedQuestions = SavedQuestionsCache()
    private var numberOfQuestions = savedQuestions.size

    private lateinit var recyclerView: RecyclerView
    private lateinit var user : Model.User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_questions, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = DatabaseManager.user ?: Model.User()
        if (user.userId.isNotEmpty() && numberOfQuestions != 0) {
            val layoutManager = LinearLayoutManager(context)
            recyclerView = view.findViewById(R.id.recycler_forum)
            recyclerView.layoutManager = layoutManager

            // display saved questions
            val adapter = ForumAdapter(savedQuestions.toList())
            recyclerView.adapter = adapter

            // move to QuestionDetails when clicking on specific question
            adapter.onItemClick = {q ->
                val intent = Intent(this.context, QuestionDetailsActivity::class.java)
                intent.putExtra("question", q)
                startActivity(intent)
            }
        } else {
            val questions : RecyclerView = view.findViewById(R.id.recycler_saved_questions)
            questions.visibility = View.GONE

            val text : TextView = view.findViewById(R.id.text_login_to_save)
            text.visibility = View.VISIBLE

            // user is logged in but no questions to display
            if (user.userId.isNotEmpty()) {
                text.text = "No saved questions."
            }
        }
    }

}