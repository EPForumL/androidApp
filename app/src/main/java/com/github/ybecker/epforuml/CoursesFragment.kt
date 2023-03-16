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
import com.github.ybecker.epforuml.authentication.AuthenticatorManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model.*

class CoursesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CourseAdapter
    private lateinit var user: User
    private lateinit var userSubscriptions: MutableList<Course>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_courses, container, false)
        recyclerView = rootView.findViewById(R.id.recyclerViewCourses)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val userId = AuthenticatorManager.getAuthenticator().user?.userId
        user = db.getUserById(userId ?: "") ?: User()

        userSubscriptions = db.getUserSubscriptions(user).toMutableList()
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

        //this methode populate the recycler view with "item_course"
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
            val switch = itemView.findViewById<Switch>(R.id.subscriptionSwitch)

            // we set u
            switch.setOnCheckedChangeListener { _, isChecked ->
                val course = itemView.tag as? Course
                if(course != null){
                    if(isChecked){
                        db.addSubscription(user, course)
                        Toast.makeText(itemView.context, "You subscribed to "+ course.courseName, Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(itemView.context, "You unsubscribed to "+ course.courseName, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            return CourseViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
            val course = courses[position]
            holder.bind(course, userSubscriptions.map { it.courseId }.contains(course.courseId))
            holder.itemView.tag = course
            holder.itemView.setOnClickListener { onCourseClickListener(course) }
        }

        override fun getItemCount() = courses.size

    }

    private inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseTitleTextView = itemView.findViewById<TextView>(R.id.courseTitleTextView)
        private val subscriptionSwitch = itemView.findViewById<Switch>(R.id.subscriptionSwitch)

        fun bind(course: Course, isSubscribed: Boolean) {
            courseTitleTextView.text = course.courseName
            subscriptionSwitch.isChecked = isSubscribed
        }
    }

    private fun onCourseClick(course: Course) {
        //for later
        println("Clicked course: ${course.courseName}")
    }

}