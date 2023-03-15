package com.github.ybecker.epforuml

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model.*

class CoursesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CourseAdapter
    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_courses, container, false)
        recyclerView = rootView.findViewById(R.id.recyclerViewCourses)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        //TODO CHANGE
        DatabaseManager.useMockDatabase()
        val db = DatabaseManager.db

        user = User()

        adapter = CourseAdapter(db.availableCourses().toList()) { course ->
            onCourseClick(course)
        }
        recyclerView.adapter = adapter
        return rootView
    }

    private inner class CourseAdapter(
        private val courses: List<Course>,
        private val onCourseClickListener: (Course) -> Unit
    ) : RecyclerView.Adapter<CourseViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
            val switch = itemView.findViewById<Switch>(R.id.subscriptionSwitch)

            switch.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    val courseName = itemView.findViewById<TextView>(R.id.courseTitleTextView).text
                    Toast.makeText(itemView.context, "You subscribed to "+ courseName, Toast.LENGTH_SHORT).show()
                }
                else{
                    val courseName = itemView.findViewById<TextView>(R.id.courseTitleTextView).text
                    Toast.makeText(itemView.context, "You unsubscribed to "+ courseName, Toast.LENGTH_SHORT).show()
                }
            }

            return CourseViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
            val course = courses[position]
            holder.bind(course)
            holder.itemView.setOnClickListener { onCourseClickListener(course) }
        }

        override fun getItemCount() = courses.size

    }

    private inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseTitleTextView = itemView.findViewById<TextView>(R.id.courseTitleTextView)

        fun bind(course: Course) {
            courseTitleTextView.text = course.courseName
        }
    }

    private fun onCourseClick(course: Course) {
        println("Clicked course: ${course.courseName}")
    }

}