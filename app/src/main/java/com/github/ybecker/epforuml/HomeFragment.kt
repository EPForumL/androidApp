package com.github.ybecker.epforuml

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model.*
import android.widget.ImageButton
import android.widget.TextView
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import java.util.concurrent.CompletableFuture

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 * Hosts the RecyclerView displaying all questions
 */
class HomeFragment(private val mainActivity: MainActivity) : Fragment() {
    // Declare variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyQuestionsAdapter
    private val user = DatabaseManager.user
    private var questionsMap = mutableMapOf<Model.Course, List<Model.Question>>()
    private lateinit var futureCourseList: CompletableFuture<List<Course>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Get the list of available courses from the database
        futureCourseList = db.availableCourses()

        // Set up the new question button and navigate to the new question fragment when clicked
        val newQuestionButton = view.findViewById<ImageButton>(R.id.new_question_button)
        newQuestionButton.setOnClickListener {
            mainActivity.replaceFragment(NewQuestionFragment(mainActivity))
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the recycler view
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recycler_my_questions)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(false)

        // Refresh the display
        refresh()
    }

    // Fetch the questions and the corresponding courses and display them in the recycler view
    private fun getForumQuestionsMap() {
        db.getQuestions().thenAccept { questions ->
            // Get the list of unique course IDs from the questions list
            val courseIds = questions.map { question -> question.courseId }.toSet().toList()

            // Create a list of futures for fetching the courses corresponding to the course IDs
            val futureCourses = mutableListOf<CompletableFuture<Model.Course?>>()
            for (id in courseIds) {
                futureCourses.add(db.getCourseById(id))
            }

            // When all courses are fetched, store the questions and display them
            CompletableFuture.allOf(*futureCourses.toTypedArray()).thenAccept {
                questionsMap = mutableMapOf()
                futureCourses.let {
                    it.forEach { futureCourse ->
                        val course = futureCourse.get()
                        if (course != null) {
                            // Filter the questions corresponding to each course and store the result in the questions map
                            val courseQuestion = questions.filter { question -> question.courseId == course.courseId }
                            questionsMap.set(course, courseQuestion)
                        }
                    }
                }

                // Update the recycler view adapter with the questions map
                questionsDisplay()
            }
        }
    }

    // Display the questions in the recycler view or a message if there are no questions
    private fun questionsDisplay() {
        if (questionsMap.isEmpty()) {
            // Display a message if there are no questions
            val message = "There is no questions yet."
            val messageView = view?.findViewById<TextView>(R.id.no_question)
            messageView?.text = message
            messageView?.visibility = View.VISIBLE
        }

        // Update the recycler view adapter with the questions map
        adapter = MyQuestionsAdapter(questionsMap)
        recyclerView.adapter = adapter

    }

    // Fetch the questions and refresh the display
    private fun refresh() {
        getForumQuestionsMap()
    }
}
