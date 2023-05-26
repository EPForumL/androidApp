package com.github.ybecker.epforuml

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.Model

// Adapter for displaying a user's questions organized by course
class MyQuestionsAdapter(private val myQuestionsMap: MutableMap<Model.Course, List<Model.Question>>,
                         private val cache: ArrayList<Model.Question>,
                         private val answersCache: ArrayList<Model.Answer>,
                         private val allQuestions: ArrayList<Model.Question>,
                         private val allAnswers: ArrayList<Model.Answer>,
                         private val allCourses: ArrayList<Model.Course>,
                         private val fragment: String) :
    RecyclerView.Adapter<MyQuestionsAdapter.MyQuestionsViewHolder>() {

    // Click listener for the forum questions
    var onItemClick :((Model.Question) -> Unit)? = null

    // Inflate the layout for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyQuestionsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_my_questions, parent, false)
        return MyQuestionsViewHolder(itemView)
    }

    // Return the number of items in the RecyclerView
    override fun getItemCount(): Int {
        return myQuestionsMap.size
    }

    // Bind the data to each item in the RecyclerView
    override fun onBindViewHolder(holder: MyQuestionsViewHolder, position: Int) {
        // Get the course for the current item
        val course = myQuestionsMap.keys.elementAt(position)

        // Set the course title
        holder.courseTitle.text = course.courseName

        // Get the list of questions for the current course
        val questionsList = myQuestionsMap[course]?.toMutableList()

        if (questionsList!!.size > 0) {
            // Set up the RecyclerView with the list of questions
            holder.forumAdapter = ForumAdapter(questionsList)
            holder.questionsRecyclerView.adapter = holder.forumAdapter
            holder.questionsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)

            // Set up the click listener for each question in the RecyclerView
            holder.forumAdapter.onItemClick = { question ->
                val intent = Intent(holder.itemView.context, QuestionDetailsActivity::class.java)
                intent.putParcelableArrayListExtra("savedQuestions", cache)
                intent.putParcelableArrayListExtra("savedAnswers", answersCache)

                // TODO : check
                intent.putParcelableArrayListExtra("allQuestions", allQuestions)
                intent.putParcelableArrayListExtra("allAnswers", allAnswers)
                intent.putParcelableArrayListExtra("allCourses", allCourses)

                intent.putExtra("comingFrom", fragment)
                intent.putExtra("question", question)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    // ViewHolder class for caching views for each item in the RecyclerView
    class MyQuestionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseTitle: TextView = itemView.findViewById(R.id.course_title_text_view)
        val questionsRecyclerView: RecyclerView = itemView.findViewById(R.id.recycler_my_questions)
        lateinit var forumAdapter: ForumAdapter
    }
}
