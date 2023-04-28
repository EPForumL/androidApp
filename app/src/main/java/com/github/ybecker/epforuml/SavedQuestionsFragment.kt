package com.github.ybecker.epforuml

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.ybecker.epforuml.notifications.NotificationType
import com.github.ybecker.epforuml.notifications.PushNotificationService


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
        PushNotificationService().sendNotification(this.requireContext(),"someone", "questionTitle", "questionText" , "test", NotificationType.QUESTION)
        return inflater.inflate(R.layout.fragment_saved_questions, container, false)
    }
}