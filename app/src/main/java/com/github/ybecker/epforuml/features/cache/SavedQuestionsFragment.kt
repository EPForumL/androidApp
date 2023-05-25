package com.github.ybecker.epforuml.features.cache

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.github.ybecker.epforuml.basicEntities.questions.ForumAdapter
import com.github.ybecker.epforuml.basicEntities.questions.QuestionDetailsActivity


/**
 * A simple [Fragment] subclass.
 * Use the [SavedQuestionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SavedQuestionsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var user : Model.User

    private lateinit var cache : ArrayList<Model.Question>
    private lateinit var answersCache : ArrayList<Model.Answer>
    private lateinit var newIntentDetails : Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cache = this.requireArguments().getParcelableArrayList("savedQuestions")!!
        answersCache = this.requireArguments().getParcelableArrayList("savedAnswers")!!

        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_saved_questions, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize recycler with saved questions
        user = DatabaseManager.user ?: Model.User()
        val userId = user.userId

        if (userId.isNotEmpty() && cache.isNotEmpty()) {
            val layoutManager = LinearLayoutManager(context)
            recyclerView = view.findViewById(R.id.recycler_saved_questions)
            recyclerView.layoutManager = layoutManager

            // display saved questions
            val adapter = ForumAdapter(cache)
            recyclerView.adapter = adapter

            // move to QuestionDetails when clicking on specific question
            adapter.onItemClick = {q ->
                newIntentDetails = Intent(context?.applicationContext, QuestionDetailsActivity::class.java)
                newIntentDetails.putParcelableArrayListExtra("savedQuestions", cache)
                newIntentDetails.putParcelableArrayListExtra("savedAnswers", answersCache)
                newIntentDetails.putExtra("comingFrom", "SavedQuestionsFragment")
                newIntentDetails.putExtra("question", q)
                startActivity(newIntentDetails)
            }

        } else {
            // if no question to display
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