package com.github.ybecker.epforuml

import android.content.Intent
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
import com.github.ybecker.epforuml.database.Model.*
import android.widget.ImageButton
import java.util.concurrent.CompletableFuture

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment(val mainActivity: MainActivity) : Fragment() {

    private lateinit var adapter : ForumAdapter
    private lateinit var recyclerView: RecyclerView
    private var questionsList = mutableListOf<Question>() // switch to questions when able to transfer data from mainActivtiy
    private lateinit var futureCourseList: CompletableFuture<List<Course>>

    /*
    lateinit var questions = Array<String> ??? // is this needed ?
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        futureCourseList = db.availableCourses()

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
        //getQuestionsQuery()

        futureCourseList.thenAccept {
            val futureQuestionList =  mutableListOf<CompletableFuture<List<Question>>>()
            for (course in it){
                futureQuestionList.add(db.getCourseQuestions(course.courseId))
            }
            CompletableFuture.allOf(*futureQuestionList.toTypedArray()).thenAccept {
                futureQuestionList.let { it.forEach { questionsList.addAll(it.get()) } }
                val layoutManager = LinearLayoutManager(context)
                recyclerView = view.findViewById(R.id.recycler_forum)
                recyclerView.layoutManager = layoutManager
                recyclerView.setHasFixedSize(true) // maybe change that later

                adapter = ForumAdapter(questionsList)
                recyclerView.adapter = adapter

                // move to QuestionDetails when clicking on specific question
                adapter.onItemClick = {
                    val intent = Intent(this.context, QuestionDetailsActivity::class.java)
                    intent.putExtra("question", it)
                    startActivity(intent)
                }
            }
        }

    }


    fun getQuestionsQuery() {
        db.availableCourses().thenAccept {
            // reset questionsList for refresh
            questionsList = mutableListOf()

            for (course in it) {
                db.getCourseQuestions(course.courseId).thenAccept {
                    questionsList.addAll(it)
                }
            }
        }

    }

}