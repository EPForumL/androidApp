package com.github.ybecker.epforuml

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.github.ybecker.epforuml.database.DatabaseManager.db
import java.io.IOException
import java.net.URL
import java.util.concurrent.CompletableFuture

@SuppressLint("StaticFieldLeak")
object NotificationUtils {

    lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    @JvmStatic
    fun sendNotification(authorId: String, receiverIds: List<String>, title: String, text: String) {
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
            db.getUserById(authorId).thenAccept {

                //val URI = it?.profilePic

                // set initial user icon
                //var bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.nav_account)

//                if(URI?.isNotEmpty() == true){
//                    try {
//                        // On essaye de charger l'image à partir de l'URI
//                        val connection = URL(URI).openConnection()
//                        connection.connect()
//                        val inputStream = connection.getInputStream()
//                        bitmap = BitmapFactory.decodeStream(inputStream)
//                        inputStream.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }

                // Créer la notification
                val notificationBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.nav_chat)
                    .setContentTitle("New question from "+it?.username)
                    .setContentText(title)
                    //.setLargeIcon(bitmap)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)


                with(NotificationManagerCompat.from(context)) {
                    // notificationId is a unique int for each notification that you must define
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {

                        notify(0,notificationBuilder.build())
                    }
                }
            }
        }
    }

}