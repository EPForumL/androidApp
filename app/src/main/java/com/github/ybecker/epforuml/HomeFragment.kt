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
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyQuestionsAdapter
    private val user = DatabaseManager.user
    private var questionsMap = mutableMapOf<Model.Course, List<Model.Question>>()
    private lateinit var futureCourseList: CompletableFuture<List<Course>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        futureCourseList = db.availableCourses()

        //DatabaseManager.useMockDatabase()


        val newQuestionButton = view.findViewById<ImageButton>(R.id.new_question_button)
        // Set click listener for the circular button with the "+" sign
        newQuestionButton.setOnClickListener {
            // Navigate to the new fragment to add a new question
            mainActivity.replaceFragment(NewQuestionFragment(mainActivity))
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recycler_my_questions)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(false)



        refresh()

    }

    // Fetch the questions and the corresponding courses and display them in the recycler view
    private fun getForumQuestionsMap() {

        db.getQuestions().thenAccept { questions ->
            val courseIds = questions.map { question -> question.courseId }.toSet().toList()
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
                            val courseQuestion = questions.filter { question -> question.courseId == course.courseId }
                            questionsMap.set(course, courseQuestion)
                        }
                    }
                }

                questionsDisplay()
            }
        }

    }



    // Display the questions in the recycler view or a message if there are no questions
    private fun questionsDisplay() {
        if (questionsMap.isEmpty()) {
            val message = "There is no questions yet."
            val messageView = view?.findViewById<TextView>(R.id.no_question)
            messageView?.text = message
            messageView?.visibility = View.VISIBLE
        }

        adapter = MyQuestionsAdapter(questionsMap)
        recyclerView.adapter = adapter



        // move to QuestionDetails when clicking on specific question

    }

    // Fetch the questions and refresh the display
    private fun refresh() {
        getForumQuestionsMap()
    }
}