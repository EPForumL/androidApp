
package com.github.ybecker.epforuml.notifications

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class RemoteNotificationService : FirebaseMessagingService() {

/*
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Get data from the message
        val author = remoteMessage.data["author"]
        val title = remoteMessage.data["title"]
        val text = remoteMessage.data["text"]
        //val uri = remoteMessage.data["URI"]
        val channelId = "my_channel_id"

        // creat new channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager: NotificationManager =
                this.application.applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Afficher une notification
        val notification = NotificationCompat.Builder(this.application.applicationContext, channelId)
        .setSmallIcon(R.drawable.nav_chat)
        .setContentTitle(author)
        .setContentText(title)
        //.setLargeIcon(bitmap)
        .setStyle(NotificationCompat.BigTextStyle().bigText(title+"\n"+text))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

        val notificationManager = NotificationManagerCompat.from(this.application.applicationContext)

        if (ActivityCompat.checkSelfPermission(
                this.application.applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(0, notification)
        }
    }

 */
}