
package com.github.ybecker.epforuml.Notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.ybecker.epforuml.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class RemoteNotificationService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Récupérer le titre et le corps du message
        val author = remoteMessage.data["author"]
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        val channelId = "my_channel_id"

        // Créer le canal de notification (disponible uniquement pour Android Oreo et versions supérieures)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager: NotificationManager =
                FirebaseCouldMessagingAdapter.context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }

        // Afficher une notification
        val notification = NotificationCompat.Builder(FirebaseCouldMessagingAdapter.context, channelId)
        .setSmallIcon(R.drawable.nav_chat)
        .setContentTitle(author)
        .setContentText(title)
        //.setLargeIcon(bitmap)
        .setStyle(NotificationCompat.BigTextStyle().bigText(title+"\n"+body))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(0, notification)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        //TODO
        // Enregistrement du token dans la base de données
        // ...

        // Envoi du token au serveur d'application si nécessaire
        // ...
    }
}