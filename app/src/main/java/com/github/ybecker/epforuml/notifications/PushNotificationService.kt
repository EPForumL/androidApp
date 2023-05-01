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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.ybecker.epforuml.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class PushNotificationService: FirebaseMessagingService() {

    private val key = "AAAA8xD8Ax0:APA91bGp-Wt6U5tALnHdk83vTF_oQzP8AqtgC4TXuiuR0m2Z3_2nm4TLJhaUjqBIL_9f14LLKSEM-SUYDuJX0n7thc9h66kFFe-HLAh-j6hWzhu-guKV8zAmqKq0jsEwuo-mXFl2dEmt"

    override fun onMessageReceived(message: RemoteMessage) {

        val notif_data = message.data
        val type = notif_data["type"]
        val author = notif_data["author"]
        val title = notif_data["title"]
        val text = notif_data["text"]

        val channelId = "NOTIFICATION_CHANNEL"
        val notifChannel = NotificationChannel(channelId, "NewNotification",
            NotificationManager.IMPORTANCE_DEFAULT)

        getSystemService(NotificationManager::class.java).createNotificationChannel(notifChannel)

        val notif = NotificationCompat.Builder(this, channelId)
            .setContentTitle("New $type from $author")
            .setContentText(title)
            .setSmallIcon(R.drawable.notification)
            .setStyle(NotificationCompat.BigTextStyle().bigText(title+"\n"+text))
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

    fun sendNotification(context: Context, from: String, title:String ,text: String, topic: String, type: NotificationType) {

        val notification = JSONObject()
        val notificationBody = JSONObject()

        notificationBody.put("author", from)
        notificationBody.put("title", title)
        notificationBody.put("text", text)
        notificationBody.put("type", type.getName())

        notification.put("to", "/topics/$topic")
        notification.put("data", notificationBody)

        val request = MyJsonObjectRequest(
            Request.Method.POST, "https://fcm.googleapis.com/fcm/send", notification,
            {
                Log.d(TAG,"response received")
            },
            {
                Log.d(TAG,"error received")
            })

        Volley.newRequestQueue(context).add(request)

        Log.d(TAG, "Send request : $request")
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
