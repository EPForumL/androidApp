package com.github.ybecker.epforuml

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.firebase.ui.auth.data.model.User
import com.github.ybecker.epforuml.database.DatabaseManager.db
import java.util.concurrent.CompletableFuture

class MyQuestionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyQuestionsAdapter
    private val user = DatabaseManager.user
    private var myQuestionsMap = mutableMapOf<Model.Course, List<Model.Question>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_questions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recycler_my_questions)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(false)

        // If user is not logged in, display a message
        if (user == null) {
            val message = "You need to be logged in to view your questions."
            val messageView = view.findViewById<TextView>(R.id.not_connected_text_view)
            messageView.text = message
            messageView.visibility = View.VISIBLE
        } else {
            refresh()
        }
    }

    // Fetch the questions and the corresponding courses and display them in the recycler view
    private fun getMyQuestionsMap() {
        if (DatabaseManager.user == null) {
            val notConnected = view?.findViewById<TextView>(R.id.not_connected_text_view)
            notConnected?.visibility = View.VISIBLE
        } else {
            val userId = user?.userId
            if (userId != null) {
                db.getUserQuestions(userId).thenAccept { questions ->
                    val courseIds = questions.map { question -> question.courseId }.toSet().toList()
                    val futureCourses = mutableListOf<CompletableFuture<Model.Course?>>()

                    for (id in courseIds) {
                        futureCourses.add(db.getCourseById(id))
                    }

                    // When all courses are fetched, store the questions and display them
                    CompletableFuture.allOf(*futureCourses.toTypedArray()).thenAccept {
                        myQuestionsMap = mutableMapOf()
                        futureCourses.let {
                            it.forEach { futureCourse ->
                                val course = futureCourse.get()
                                if (course != null) {
                                    val courseQuestion = questions.filter { question -> question.courseId == course.courseId }
                                    myQuestionsMap.set(course, courseQuestion)
                                }
                            }
                        }

                        myQuestionsDisplay()
                    }
                }
            }
        }
    }

    // Display the questions in the recycler view or a message if there are no questions
    private fun myQuestionsDisplay() {
        if (myQuestionsMap.isEmpty()) {
            val message = "You have no questions yet."
            val messageView = view?.findViewById<TextView>(R.id.no_question)
            messageView?.text = message
            messageView?.visibility = View.VISIBLE
        }

        adapter = MyQuestionsAdapter(myQuestionsMap)
        recyclerView.adapter = adapter
    }

    // Fetch the questions and refresh the display
    private fun refresh() {
        getMyQuestionsMap()
    }
}


