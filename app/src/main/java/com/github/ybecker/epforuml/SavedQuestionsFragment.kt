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
import com.github.ybecker.epforuml.cache.LocalCache
import com.github.ybecker.epforuml.cache.MockSavedQuestionsCache
import com.github.ybecker.epforuml.cache.SavedQuestionsCache
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text


/**
 * A simple [Fragment] subclass.
 * Use the [SavedQuestionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SavedQuestionsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var user : Model.User

    private lateinit var testtext : String
    private lateinit var newIntentMain : Intent
    private lateinit var newIntentDetails : Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        testtext = this.arguments?.getString("testtext") ?: "FAIL IN SAVED"
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_questions, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize recycler with saved questions
        user = DatabaseManager.user ?: Model.User()
        val userId = user.userId

        val testTextView : TextView = view.findViewById(R.id.test_testview)
        testTextView.text = testtext

        // TODO check if still in main
        newIntentMain = Intent(context?.applicationContext, MainActivity::class.java)
        intent.putExtra("testtext", testtext)

        newIntentDetails = Intent(context?.applicationContext, QuestionDetailsActivity::class.java)
        newIntentDetails.putExtra("testtext", testtext)

        val list = LocalCache().getSavedQuestions().toList()

        if (userId.isNotEmpty() && list.isNotEmpty()) {
            val layoutManager = LinearLayoutManager(context)
            recyclerView = view.findViewById(R.id.recycler_saved_questions)
            recyclerView.layoutManager = layoutManager

            // display saved questions
            val adapter = ForumAdapter(list)
            recyclerView.adapter = adapter

            // move to QuestionDetails when clicking on specific question
            adapter.onItemClick = {q ->
                //val intent = Intent(this.context, QuestionDetailsActivity::class.java)
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