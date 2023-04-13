package com.github.ybecker.epforuml.Notifications

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

    lateinit var context: Context

    fun init(context: Context) {
        FirebaseCouldMessagingAdapter.context = context.applicationContext
    }

    @JvmStatic
    fun sendQuestionNotifications(question: Question) {
        if(context != null){
            // Créer un identifiant unique pour le canal de notification
//            val channelId = "my_channel_id"
//
//            // Créer le canal de notification (disponible uniquement pour Android Oreo et versions supérieures)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
//                val notificationManager: NotificationManager =
//                    context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//                notificationManager?.createNotificationChannel(channel)
//            }
            val futureAuthor = db.getUserById(question.userId)
            val futureTokenList = db.getCourseNotificationTokens(question.courseId)
            CompletableFuture.allOf(futureAuthor, futureTokenList).thenAccept {

                val author = futureAuthor.get()
                val tokenList = futureTokenList.get()

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

//                // Créer la notification
//                val notification = NotificationCompat.Builder(context, channelId)
//                    .setSmallIcon(R.drawable.nav_chat)
//                    .setContentTitle("New question from "+author?.username)
//                    .setContentText(question.questionTitle)
//                    //.setLargeIcon(bitmap)
//                    .setStyle(NotificationCompat.BigTextStyle().bigText(question.questionTitle+"\n"+question.questionText))
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .build()
//
//                with(NotificationManagerCompat.from(context)) {
//                    // notificationId is a unique int for each notification that you must define
//                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
//                        == PackageManager.PERMISSION_GRANTED) {
//
//                        //notify(0,notification)
//                    }
//                }

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
            }
        }
    }

}