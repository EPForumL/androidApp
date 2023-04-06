package com.github.ybecker.epforuml

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService


@SuppressLint("StaticFieldLeak")
object NotificationUtils {

    lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    @JvmStatic
    fun sendNotification(title: String, authorId: String, text: String) {
        if(context != null){
            // Créer un identifiant unique pour le canal de notification
            val channelId = "my_channel_id"

            // Créer le canal de notification (disponible uniquement pour Android Oreo et versions supérieures)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
                val notificationManager: NotificationManager =
                    context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager?.createNotificationChannel(channel)
            }
            // Créer la notification
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.nav_chat)
                .setContentTitle(title)
                .setContentText(text)
                // set user avatar .setLargeIcon(emailObject.getSenderAvatar())
                //.setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)


            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    notify(0, notificationBuilder.build())
                    return
                }
            }
        }
    }

}