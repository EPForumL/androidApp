package com.github.ybecker.epforuml

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass.
 * Use the [CourseQuestionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CourseQuestionsFragment : Fragment() {

    private lateinit var courseName: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get the course name from the arguments
        courseName = arguments?.getString("courseName") ?: ""

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_course_questions, container, false)

        val tv = rootView.findViewById<TextView>(R.id.CourseQuestionTitle)
        tv.setText(courseName+"'s Questions")
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Query your database to get the questions for the selected course and display them in the fragment
    }

}