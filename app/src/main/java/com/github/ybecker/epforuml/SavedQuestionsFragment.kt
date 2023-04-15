package com.github.ybecker.epforuml

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.ybecker.epforuml.notifications.FirebaseCouldMessagingAdapter
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import com.github.ybecker.epforuml.notifications.RemoteNotificationService
import com.google.firebase.messaging.RemoteMessage


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

        val message = RemoteMessage.Builder("osef")
            .setMessageId(Integer.toString(0))
            .addData("author", "New question from Theo")
            .addData("title", "Ma question !")
            .addData("text", "merci de bien vouloir repondre")
            //.addData("icon", notification.extras.getString(NotificationCompat.EXTRA_SMALL_ICON))
            //.addData("click_action", notification.extras.getString(NotificationCompat.EXTRA_NOTIFICATION_CLICK_ACTION))
            .build()

        Thread(Runnable {
            RemoteNotificationService().onMessageReceived(message)
        }).start()

        //FirebaseCouldMessagingAdapter.sendQuestionNotifications(Model.Question("hihi", "-NT4bmu0pHU3XPM6qx7W","0", "NEW QUESTION", "JSPR QUE CA MARCHE","", emptyList()))

        return inflater.inflate(R.layout.fragment_saved_questions, container, false)

    }
}