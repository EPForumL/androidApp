//package com.github.ybecker.epforuml.Notifications
//
//import androidx.test.core.app.ActivityScenario
//import androidx.test.espresso.matcher.ViewMatchers.assertThat
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.github.ybecker.epforuml.MainActivity
//import com.github.ybecker.epforuml.database.DatabaseManager
//
//import com.github.ybecker.epforuml.notifications.NotificationType
//import com.github.ybecker.epforuml.notifications.PushNotificationService
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.messaging.RemoteMessage
//import junit.framework.TestCase.assertTrue
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class PushNotificationServiceTest {
//
////    var logs = ""
////
////    @Test
////    fun testOnMessageReceivedIsCalledWithCorrectMessage() {
////        DatabaseManager.useMockDatabase()
////        Firebase.auth.signOut()
////        val scenario = ActivityScenario.launch(MainActivity::class.java)
////
////        scenario.onActivity { activity ->
////            val context = activity.applicationContext
////
////            PushNotificationService().sendNotification(context,"Test1", "Test2", "Test3", "Test4", NotificationType.ANSWER)
////        }
////    }
////
////    inner class MockPushNotificationService: PushNotificationService() {
////        override fun onMessageReceived(message: RemoteMessage) {
////
////            logs = "$message"
////
////            super.onMessageReceived(message)
////        }
////    }
//
//}