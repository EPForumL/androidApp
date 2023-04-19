package com.github.ybecker.epforuml.notifications

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import java.util.concurrent.CompletableFuture

@SuppressLint("StaticFieldLeak")
object FirebaseCouldMessagingAdapter {


    @JvmStatic
    fun sendQuestionNotifications(question: Question) {
        val futureAuthor = db.getUserById(question.userId)
        val futureTokenList = db.getCourseNotificationTokens(question.courseId)
        CompletableFuture.allOf(futureAuthor, futureTokenList).thenAccept {

            val author = futureAuthor.get()
            val tokenList = futureTokenList.get()
            val random = Random()

            FirebaseMessaging.getInstance().token.addOnSuccessListener {token ->
                val message = RemoteMessage.Builder(token)
                    .setMessageId(Integer.toString(random.nextInt()))
                    .addData("author", "New question from "+author?.username)
                    .addData("title", question.questionTitle)
                    .addData("body", question.questionText)
                    //.addData("icon", notification.extras.getString(NotificationCompat.EXTRA_SMALL_ICON))
                    //.addData("click_action", notification.extras.getString(NotificationCompat.EXTRA_NOTIFICATION_CLICK_ACTION))
                    .build()

                try {
                    FirebaseMessaging.getInstance().send(message)
                    Log.d(TAG, "Message sent to $token")
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending message to $token: $e")
                }
            }

            for (token in tokenList){
                val message = RemoteMessage.Builder(token)
                    .setMessageId(Integer.toString(random.nextInt()))
                    .addData("author", "New question from "+author?.username)
                    .addData("title", question.questionTitle)
                    .addData("text", question.questionText)
                    //.addData("icon", notification.extras.getString(NotificationCompat.EXTRA_SMALL_ICON))
                    //.addData("click_action", notification.extras.getString(NotificationCompat.EXTRA_NOTIFICATION_CLICK_ACTION))
                    .build()

                try {
                    FirebaseMessaging.getInstance().send(message)

                    Thread(Runnable {
                        RemoteNotificationService().onMessageReceived(message)
                    }).start()

                    Log.d(TAG, "Message sent to $token")
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending message to $token: $e")
                }
            }
        }
    }

}