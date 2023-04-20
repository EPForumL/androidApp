package com.github.ybecker.epforuml

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ybecker.epforuml.MyQuestionsAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.data.model.User

import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import java.util.concurrent.CompletableFuture
import android.util.Log


/**
 * A simple [Fragment] subclass.
 * Use the [MyQuestionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class MyQuestionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyQuestionsAdapter
    val user = DatabaseManager.user
    private var myQuestionsMap = mutableMapOf<Model.Course, List<Model.Question>>() // switch to questions when able to transfer data from mainActivtiy




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //DatabaseManager.useMockDatabase()
        //getMyQuestionsMap()
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_questions, container, false)

        //Uncomment
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recycler_my_questions)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true) // maybe change that later



        if (user == null) {
            // User is not logged in, display a message
            val message = "You need to be logged in to view your questions."
            val messageView = view.findViewById<TextView>(R.id.not_connected_text_view)
            messageView.text = message
            messageView.visibility = View.VISIBLE
        } else {
            refresh()
        }

    }


    private fun getMyQuestionsMap() {
        //user?.let { user ->
        if (DatabaseManager.user == null) {
            val notConnected = view?.findViewById<TextView>(R.id.not_connected_text_view)
            notConnected?.visibility = View.VISIBLE
        }
        else {
        val userId = user?.userId
            if (userId != null){

            db.getUserQuestions(userId).thenAccept { questions ->
                //myQuestionsMap = mutableMapOf<Model.Course, MutableList<Model.Question>>()

                val courseIds = questions.map { question -> question.courseId }.toSet().toList()
                val futureCourses = mutableListOf<CompletableFuture<Model.Course?>>()
                for (id in courseIds) {
                    futureCourses.add(db.getCourseById(id))
                }
                //complete quand liste de future a complete
                CompletableFuture.allOf(*futureCourses.toTypedArray()).thenAccept {
                    myQuestionsMap = mutableMapOf()
                    futureCourses.let {
                        it.forEach { futureCourse ->
                            val course = futureCourse.get()
                            if (course != null) {
                                val courseQuestion =
                                    questions.filter { question -> question.courseId == course.courseId }
                                myQuestionsMap.set(course, courseQuestion)
                            }
                        }
                    }
                    //myQuestionsMap = mutableMapOf(Pair(Model.Course("course11","Database", mutableListOf()), mutableListOf<Model.Question>(Model.Question("question2", "course0", "user1", "About Scrum master",
                    //    "What is a Scrum Master ?", "" , mutableListOf())) ))

                    myQuestionsDisplay()
            }
        }
        }


        }
        //  }
    }


    private fun myQuestionsDisplay() {


        if (myQuestionsMap.isEmpty()){
            val message = "You have no questions yet."
            val messageView = view?.findViewById<TextView>(R.id.no_question)
            messageView?.text = message
            messageView?.visibility = View.VISIBLE
        }

        adapter = MyQuestionsAdapter(myQuestionsMap)
        recyclerView.adapter = adapter

        //getMyQuestionsMap()
        //adapter = MyQuestionsAdapter(myQuestionsMap)

        //recyclerView.adapter = adapter

        // move to QuestionDetails when clicking on specific question
    }

    private fun refresh() {
        getMyQuestionsMap()

    }

}



