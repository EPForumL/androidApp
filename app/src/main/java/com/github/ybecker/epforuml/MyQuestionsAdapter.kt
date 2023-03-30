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

class MyQuestionsAdapter(private val myQuestionsMap: MutableMap<Model.Course, List<Model.Question>>) : RecyclerView.Adapter<MyQuestionsAdapter.MyQuestionsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyQuestionsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_my_questions, parent, false)
        return MyQuestionsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return myQuestionsMap.size
    }

    override fun onBindViewHolder(holder: MyQuestionsViewHolder, position: Int) {
        val course = myQuestionsMap.keys.elementAt(position)
        holder.courseTitle.text = course.courseId
        val questionsList = myQuestionsMap[course]?.toMutableList()

        holder.forumAdapter = ForumAdapter(questionsList ?: mutableListOf<Model.Question>())

        holder.questionsRecyclerView.adapter = holder.forumAdapter
    }

    class MyQuestionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseTitle: TextView = itemView.findViewById(R.id.course_title_text_view)
        val questionsRecyclerView: RecyclerView = itemView.findViewById(R.id.recycler_my_questions)
        lateinit var forumAdapter: ForumAdapter
    }
}