package com.github.ybecker.epforuml.Notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.media.audiofx.BassBoost
import android.os.ParcelFileDescriptor
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.system.Os.listen
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.MainActivity.Companion.context
import com.github.ybecker.epforuml.authentication.MockAuthenticator
import com.github.ybecker.epforuml.notifications.RemoteNotificationService
import com.google.firebase.messaging.RemoteMessage
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileDescriptor
import java.util.Random

@RunWith(AndroidJUnit4::class)
class RemoteNotificationServiceTest {

    @Test
    fun onMessageReceiveTestLaunchANotification(){
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        // TODO: use mock database ?
        scenario.onActivity { MockAuthenticator(it).signIn() } // TODO: if use mock db then signIn().join() (just in case)

        val context = ApplicationProvider.getApplicationContext<Context>()
        val notificationsListener = TestNotificationListener()

        notificationsListener.apply {
            //...
        }

        val random = Random()
        val testMessage = RemoteMessage.Builder("0")
            .setMessageId(Integer.toString(random.nextInt()))
            .addData("author", "New question")
            .addData("title", "Title")
            .addData("text", "testBody")
            .build()

        RemoteNotificationService().onMessageReceived(testMessage)

        // On attend que la notification soit reçue
        //var notifications: List<Notification>
        //do {
        //    notifications = notificationsListener.notifications
        //    Thread.sleep(100)
        //} while (notifications.isEmpty())

        // On vérifie que la notification est bien conforme
        //val notification = notifications[0]
        //assertThat(notification.extras.getString("android.title"), containsString("New question"))
        //assertThat(notification.extras.getString("android.text"), containsString("Title"))

    }
}

class TestNotificationListener : NotificationListenerService() {

    val notifications = mutableListOf<Notification>()

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let {
            notifications.add(sbn.notification)
        }
    }
}