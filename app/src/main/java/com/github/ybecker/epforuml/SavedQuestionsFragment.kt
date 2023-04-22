package com.github.ybecker.epforuml

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.ybecker.epforuml.database.DatabaseManager


/**
 * A simple [Fragment] subclass.
 * Use the [SavedQuestionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SavedQuestionsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        DatabaseManager.db.availableCourses().thenAccept {
            val c = it[0]
            Toast.makeText(MainActivity.context, "subscibe to "+c.courseName, Toast.LENGTH_SHORT)
            DatabaseManager.db.addStatus(DatabaseManager.user!!.userId, c.courseId, UserStatus.STUDENT_ASSISTANT)
        }
        return inflater.inflate(R.layout.fragment_saved_questions, container, false)

    }
}