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
import com.github.ybecker.epforuml.authentication.AuthenticatorManager
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import java.util.concurrent.CompletableFuture


/**
 * A simple [Fragment] subclass.
 * Use the [MyQuestionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class MyQuestionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyQuestionsAdapter
    val user = AuthenticatorManager.authenticator?.user
    private var myQuestionsMap = mutableMapOf<Model.Course, MutableList<Model.Question>>() // switch to questions when able to transfer data from mainActivtiy




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_questions, container, false)


        //Uncomment
        DatabaseManager.useMockDatabase()

            return view
        }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recycler_my_questions)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true) // maybe change that later



        if (user != null) {
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
            val userId = "user1"
            db.getUserQuestions(userId)
                .thenAccept { questions ->
                    myQuestionsMap = mutableMapOf<Model.Course, MutableList<Model.Question>>()
                    questions.forEach { question ->
                        db.getCourseById(question.courseId).thenAccept { course ->
                            val courseQuestions = myQuestionsMap.getOrDefault(course, mutableListOf())
                            course?.let {
                                courseQuestions.add(question)
                                myQuestionsMap[course] = courseQuestions
                            }
                        }
                    }

                }
      //  }
    }







    private fun myQuestionsDisplay() {
        adapter = MyQuestionsAdapter(myQuestionsMap)

        recyclerView.adapter = adapter

    // move to QuestionDetails when clicking on specific question
    }

    private fun refresh() {

        getMyQuestionsMap()

    }


}
