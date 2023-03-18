package com.github.ybecker.epforuml

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.FirebaseDatabaseAdapter
import com.github.ybecker.epforuml.database.Model
import android.widget.ImageButton

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment(val mainActivity: MainActivity) : Fragment() {

    private lateinit var adapter : ForumAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var questionsList : MutableList<Model.Question> // switch to questions when able to transfer data from mainActivtiy

    /*
    lateinit var questions = Array<String> ??? // is this needed ?
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val newQuestionButton = view.findViewById<ImageButton>(R.id.new_question_button)
        // Set click listener for the circular button with the "+" sign
        newQuestionButton.setOnClickListener {
            // Navigate to the new fragment to add a new question
            mainActivity.replaceFragment(NewQuestionFragment(mainActivity), "New Question")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // remove when using mockDB
        getQuestionsQuery()

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recycler_forum)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true) // maybe change that later

        adapter = ForumAdapter(questionsList)
        recyclerView.adapter = adapter


    }


    fun getQuestionsQuery() {
        var courseSet = db.availableCourses()

        // reset questionsList for refresh
        questionsList = mutableListOf()

        for (course in courseSet) {
            questionsList.addAll(db.getCourseQuestions(course))
        }
    }

}