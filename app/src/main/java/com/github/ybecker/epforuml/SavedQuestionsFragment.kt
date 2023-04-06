package com.github.ybecker.epforuml

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db


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

        NotificationUtils.init(requireContext())
        DatabaseManager.user?.let { db.addQuestion(it.userId, "-NSLO02aJWeSt28xmtiH", "newQuestion", "this is a new question", "") }

        return inflater.inflate(R.layout.fragment_saved_questions, container, false)

    }
}