// Importing necessary libraries for this code file
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
import android.widget.ImageButton

// A simple fragment to display the questions list.

//Initially it was supposed make a map with as keys the courses and as value the list of questions posted by
//the user in that course.
//For the moment it only displays the list of questions posted by the user in all courses.
//MyQuestionsAdapter is made to handle the display of a map, and will be used later


class MyQuestionsFragment : Fragment() {

// Declare variables
        private lateinit var forumAdapter: ForumAdapter
        private lateinit var forumRecyclerView: RecyclerView
        private var queryList = mutableListOf<Model.Question>()
        private lateinit var asyncCourseList: CompletableFuture<List<Model.Course>>

        // Called to create the fragment's UI
        override fun onCreateView(
            inflater: LayoutInflater, parentContainer: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            val fragmentView = inflater.inflate(R.layout.fragment_my_questions, parentContainer, false)

            // Utilize the mock database
            //DatabaseManager.useMockDatabase()

            // Retrieve all available courses
            asyncCourseList = db.availableCourses()

            // Return the fragment view
            return fragmentView
        }

        // Called after onCreateView returns and the fragment's view hierarchy is created
        override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
            super.onViewCreated(fragmentView, savedInstanceState)

            // Configure recycler view and adapter
            val linearLayoutMgr = LinearLayoutManager(context)
            forumRecyclerView = fragmentView.findViewById(R.id.recycler_forum)
            forumRecyclerView.layoutManager = linearLayoutMgr
            forumRecyclerView.setHasFixedSize(true)

            // Update questions list
            updateQuestions()
        }

        // Obtains and displays the list of questions
        private fun fetchQuestions() {
            //if user is not connected, display a message
            if (DatabaseManager.user == null) {
                val notConnected = view?.findViewById<TextView>(R.id.not_connected_text_view)
                notConnected?.visibility = View.VISIBLE
            } else {


                val list = DatabaseManager.user?.let {
                   DatabaseManager.db.getUserQuestions(it.userId).get()
               }

                //COMMENT THIS DEMO VERSION
                //val list = DatabaseManager.db.getUserQuestions("user1").get()
                ////COMMENT THIS



                if (list != null) {
                    if (list.isEmpty()) {
                        val noQuestions = view?.findViewById<TextView>(R.id.no_question)
                        noQuestions?.visibility = View.VISIBLE
                    } else {
                        queryList = list as MutableList<Model.Question>
                        // Show the query list
                        displayQuestions()
                    }
                }
            }
        }



        // Shows the list of questions
        private fun displayQuestions() {




            //if user has no questions, display a message
            if (queryList.isEmpty()) {
                val noQuestions = view?.findViewById<TextView>(R.id.no_question)
                noQuestions?.visibility = View.VISIBLE
            }

            else {
                forumAdapter = ForumAdapter(queryList)
                forumRecyclerView.adapter = forumAdapter

                // Navigate to QuestionDetailsActivity when a question is clicked
                forumAdapter.onItemClick = { question ->
                    val navigationIntent = Intent(this.context, QuestionDetailsActivity::class.java)
                    startActivity(navigationIntent)
                }
            }
        }

        // Update the questions list
        private fun updateQuestions() {
            fetchQuestions()
        }
    }

