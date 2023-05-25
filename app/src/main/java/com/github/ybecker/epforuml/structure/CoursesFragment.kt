package com.github.ybecker.epforuml.structure

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model.*
import com.github.ybecker.epforuml.database.UserStatus
import java.util.concurrent.CompletableFuture

class CoursesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CourseAdapter
    private lateinit var user: User
    private lateinit var userSubscriptions : List<Course>
    private lateinit var courses: List<Course>

    private var myContext: Context? = null

    private val VIEW_TYPE_NORMAL = 0
    private val VIEW_TYPE_LAST_ITEM = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        
        myContext = requireContext();

        // we take the current user in the database
        user = DatabaseManager.user ?: User()
        val rootView = inflater.inflate(R.layout.fragment_courses, container, false)
        recyclerView = rootView.findViewById(R.id.recyclerViewCourses)
        recyclerView.layoutManager = LinearLayoutManager(activity)


        if (user.userId.length==0) {
            val textView = rootView.findViewById<TextView>(R.id.notConnectedTextView)
            textView.visibility = View.VISIBLE
        }

        refreshCourses()

        return rootView
    }

    private inner class CourseAdapter(
        private val courses: List<Course>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        //this methode put the different courses in the recycler view
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == VIEW_TYPE_NORMAL) {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
                return CourseViewHolder(itemView)
            }
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_add_course, parent, false)
            return AddCourseViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(holder is CourseViewHolder){
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
            } else if(holder is AddCourseViewHolder) {

                holder.bind()

                holder.itemView.setOnClickListener {
                    val addTitle =  holder.itemView.findViewById<TextView>(R.id.courseTitleTextView)
                    val addLayout =  holder.itemView.findViewById<LinearLayout>(R.id.addCourseLayout)
                    val user = DatabaseManager.user
                    if(user != null && user.userId.isNotEmpty()) {
                        if (addLayout.visibility == View.GONE) {
                            addLayout.visibility = View.VISIBLE
                            addTitle.visibility = View.GONE
                        } else {
                            addLayout.visibility = View.GONE
                            addTitle.visibility = View.VISIBLE
                        }
                    }
                }
            }

        }

        override fun getItemCount() = courses.size + 1

        override fun getItemViewType(position: Int): Int {
            return if (position == courses.size) VIEW_TYPE_LAST_ITEM else VIEW_TYPE_NORMAL
        }

    }

    private fun refreshCourses(){
        val futureCourses = db.availableCourses()
        val futureUserSubscriptions= db.getUserSubscriptions(user.userId)
        CompletableFuture.allOf(futureCourses, futureUserSubscriptions).thenAccept {
            courses = futureCourses.get()
            userSubscriptions  = futureUserSubscriptions.get()
            // save actual user subscription in the a private variable
            adapter = CourseAdapter(courses)
            recyclerView.adapter = adapter
        }
    }

    private inner class AddCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val text = itemView.findViewById<EditText>(R.id.addCourseName)
        private val button = itemView.findViewById<ImageButton>(R.id.addCourseButton)
        fun bind() {
            button.setOnClickListener {
                if(text.text.isNotEmpty()) {
                    val newCourse = db.addCourse(text.text.toString())
                    db.addStatus(DatabaseManager.user!!.userId, newCourse.courseId,
                        UserStatus.TEACHER
                    )
                    text.setText("")
                    refreshCourses()
                    Toast.makeText(myContext, "You add a newCourse.", Toast.LENGTH_SHORT)
                } else {
                    Toast.makeText(myContext, "Course name cannot be empty.", Toast.LENGTH_SHORT)
                }
            }
        }
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
