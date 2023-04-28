package com.github.ybecker.epforuml.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class PushNotificationService: FirebaseMessagingService() {

    private val key = "BNVie0Bei4MJ-fu4xj1WOJ9VRaPbg04uaygJG_TZsclsiQkz1zbTkOkw4aMoWovC0ItFmEUPYrQC_x6LJCQc2Po"
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

    override fun onNewToken(token: String) {
        Log.d(ContentValues.TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        //TODO Deal with DB
    }

    //from: String, title:String ,body: String, topic: String
    fun sendNotification(context: Context) {

//        FirebaseMessaging.getInstance().isAutoInitEnabled = true
//
//        val topic = "test"
//        val message = RemoteMessage.Builder("1043962004253@gcm.googleapis.com")
//            .setMessageId(java.util.UUID.randomUUID().toString())
//            .addData("author", "TESTER USER")
//            .addData("title", "Hello from FCM")
//            .addData("title", "FCM Message")
//            .build()
//
//        Log.d(TAG, "SENDING MESSAGE")
//        // Send a message to the devices subscribed to the provided topic.
//        FirebaseMessaging.getInstance().send(message)

        val topic = "test"
        val notification = JSONObject()
        val notificationBody = JSONObject()

        notificationBody.put("title", "Titre de la notification")
        notificationBody.put("message", "Contenu de la notification")

        notification.put("to", "/topics/$topic")
        notification.put("data", notificationBody)

        val request = MyJsonObjectRequest(
            Request.Method.POST, "https://fcm.googleapis.com/fcm/send", notification,
            {
                // La notification a été envoyée avec succès
                Log.d(TAG,"response received")
            },
            {
                // Erreur lors de l'envoi de la notification
                Log.d(TAG,"error received")
            })

        // Ajouter la requête à la file d'attente de la bibliothèque Volley
        Volley.newRequestQueue(context).add(request)

        Log.d(TAG, "Send request")
    }


    inner class MyJsonObjectRequest(
        method: Int,
        url: String?,
        jsonRequest: JSONObject?,
        listener: Response.Listener<JSONObject>?,
        errorListener: Response.ErrorListener?
    ) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {

        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "key=$key"
            return headers
        }
    }
}