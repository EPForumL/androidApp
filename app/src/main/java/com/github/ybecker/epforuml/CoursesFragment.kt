package com.github.ybecker.epforuml

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model.*
import java.util.concurrent.CompletableFuture
import kotlin.properties.Delegates

class CoursesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CourseAdapter
    private lateinit var user: User
    private lateinit var userSubscriptions : List<Course>
    private lateinit var courses: List<Course>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val futureCourses = db.availableCourses()

        // we take the current user in the database
        user = DatabaseManager.user ?: User()

        val futureUserSubscriptions= db.getUserSubscriptions(user.userId)

        val rootView = inflater.inflate(R.layout.fragment_courses, container, false)
        recyclerView = rootView.findViewById(R.id.recyclerViewCourses)
        recyclerView.layoutManager = LinearLayoutManager(activity)


        if (user.userId.length==0) {
            val textView = rootView.findViewById<TextView>(R.id.notConnectedTextView)
            textView.visibility = View.VISIBLE
        }

        CompletableFuture.allOf(futureCourses, futureUserSubscriptions).thenAccept {
            courses = futureCourses.get()
            userSubscriptions  = futureUserSubscriptions.get()
            // save actual user subscription in the a private variable
            adapter = CourseAdapter(courses)
            recyclerView.adapter = adapter
        }
        return rootView
    }

    private inner class CourseAdapter(
        private val courses: List<Course>
    ) : RecyclerView.Adapter<CourseViewHolder>() {

        //this methode put the different courses in the recycler view
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)

            return CourseViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
                val course = courses[position]
                // call bind with name args and if the switch is checked or not
                holder.bind(course)

                // add course here so we can get it in onCreateViewHolder
                holder.itemView.tag = course
                holder.itemView.setOnClickListener {
                    val course_switches =  holder.itemView.findViewById<LinearLayout>(R.id.switch_container)
                    if(course_switches.visibility == View.GONE){
                        course_switches.visibility = View.VISIBLE
                    }
                    else{
                        course_switches.visibility = View.GONE
                    }
                }

        }

        override fun getItemCount() = courses.size

    }

    private inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseTitleTextView = itemView.findViewById<TextView>(R.id.courseTitleTextView)
        private val subscriptionSwitch = itemView.findViewById<Switch>(R.id.subscriptionSwitch)
        private val notificationSwitch = itemView.findViewById<Switch>(R.id.notificationSwitch)

        fun bind(course: Course) {
            courseTitleTextView.text = course.courseName
            subscriptionSwitch.isEnabled = user.userId.length != 0
            val is_subscribed = userSubscriptions.map { it.courseId }.contains(course.courseId)
            subscriptionSwitch.isChecked = is_subscribed
            notificationSwitch.isEnabled = is_subscribed
            db.getCourseNotificationUserIds(course.courseId).thenAccept {

                notificationSwitch.isChecked = it.contains(user.userId)

                // add a listener to every switches
                subscriptionSwitch.setOnCheckedChangeListener { _, isChecked ->
                    if(course != null){
                        if (isChecked) {
                            // add a subscription for this user in the course
                            db.addSubscription(user.userId, course.courseId)
                            notificationSwitch.isEnabled = true
                            Toast.makeText(itemView.context,"You subscribed to " + course.courseName,Toast.LENGTH_SHORT).show()
                        }
                        else {
                            db.removeSubscription(user.userId, course.courseId)
                            db.removeNotification(user.userId, course.courseId)
                            notificationSwitch.isEnabled = false
                            notificationSwitch.isChecked = false
                            Toast.makeText(itemView.context,"You unsubscribed to " + course.courseName,Toast.LENGTH_SHORT).show()

                        }
                    }
                }
                notificationSwitch.setOnCheckedChangeListener {_, isChecked ->
                    if(course != null){
                        if (isChecked) {
                            // add a subscription for this user in the course
                            db.addNotification(user.userId, course.courseId)
                            Toast.makeText(itemView.context, "You allow notifications for " + course.courseName, Toast.LENGTH_SHORT).show()
                        }
                        else {
                            db.removeNotification(user.userId, course.courseId)
                            Toast.makeText(itemView.context,"You reject notifications for " + course.courseName,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
