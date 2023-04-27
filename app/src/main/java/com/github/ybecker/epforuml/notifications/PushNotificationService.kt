package com.github.ybecker.epforuml.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.view.textclassifier.ConversationActions
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.github.ybecker.epforuml.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val received_notif = message.notification
        val title = received_notif?.title
        val body = received_notif?.body
        val channelId = "NOTIFICATION_CHANNEL"
        val notifChannel = NotificationChannel(channelId, "New Notification",
            NotificationManager.IMPORTANCE_DEFAULT)

        getSystemService(NotificationManager::class.java).createNotificationChannel(notifChannel)

        val notif = Notification.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(1, notif.build())
            return
        }
        super.onMessageReceived(message)
    }

    //from: String, title:String ,body: String, topic: String
    fun sendNotification() {

        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        val topic = "test"
        val message = RemoteMessage.Builder("/topics/$topic")
            .setMessageId(java.util.UUID.randomUUID().toString())
            .addData("author", "TESTER USER")
            .addData("title", "Hello from FCM")
            .addData("title", "FCM Message")
            .build()

        // Send a message to the devices subscribed to the provided topic.
        FirebaseMessaging.getInstance().send(message)
    }
}