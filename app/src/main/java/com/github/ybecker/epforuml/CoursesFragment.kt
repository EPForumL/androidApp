package com.github.ybecker.epforuml

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model

class CoursesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CourseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_courses, container, false)
        recyclerView = rootView.findViewById(R.id.recyclerViewCourses)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        //TODO CHANGE
        DatabaseManager.useMockDatabase()
        val db = DatabaseManager.getDatabase()

        adapter = CourseAdapter(db.availableCourses().toList()) { course ->
            onCourseClick(course)
        }
        recyclerView.adapter = adapter
        return rootView
    }

    private inner class CourseAdapter(
        private val courses: List<Model.Course>,
        private val onCourseClickListener: (Model.Course) -> Unit
    ) : RecyclerView.Adapter<CourseViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
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

        fun bind(course: Model.Course) {
            courseTitleTextView.text = course.courseName
        }
    }

    private fun onCourseClick(course: Model.Course) {
        println("Clicked course: ${course.courseName}")

        launchCourseQuestionFragment(course.courseName)
    }

    private fun launchCourseQuestionFragment(courseName: String) {
        val courseQuestionFragment = CourseQuestionsFragment()
        val bundle = Bundle()
        bundle.putString("courseName", courseName)
        courseQuestionFragment.arguments = bundle

        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.courses_layout_parent, courseQuestionFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}