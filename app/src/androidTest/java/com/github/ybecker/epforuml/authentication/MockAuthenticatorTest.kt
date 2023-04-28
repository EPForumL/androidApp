package com.github.ybecker.epforuml.authentication

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.github.ybecker.epforuml.R
import java.util.concurrent.CompletableFuture


@RunWith(AndroidJUnit4::class)
class MockAuthenticatorTest {
    lateinit var scenario: ActivityScenario<LoginActivity>

    @Before
    fun initTests() {
        Firebase.auth.signOut()
        DatabaseManager.user = null
        DatabaseManager.useMockDatabase()
        scenario = ActivityScenario.launch(LoginActivity::class.java)
        Intents.init()
    }

    @After
    fun endTests() {
        Intents.release()
    }

    @Test
    fun checkSignInAddsUserToDatabase() {
        assertTrue(DatabaseManager.user == null)
        scenario.onActivity {
            MockAuthenticator(it).signIn().join()
            assertTrue(DatabaseManager.user != null)
        }
    }

    @Test
    fun checkSignInStartsMainActivity() {
        scenario.onActivity { MockAuthenticator(it).signIn().join() }
        intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun checkUserIsNullWhenSignOut() {
        scenario.onActivity { MockAuthenticator(it).signIn().join() }
        assertTrue(DatabaseManager.user != null)
        scenario.onActivity { MockAuthenticator(it).signOut().join() }
        assertTrue(DatabaseManager.user == null)
    }

    @Test
    fun checkUserIsRemovedFromDatabaseWhenDeleted() {
        scenario.onActivity { MockAuthenticator(it).signIn().join() }
        assertTrue(DatabaseManager.user != null)
        scenario.onActivity { MockAuthenticator(it).deleteUser().join() }
        assertTrue(DatabaseManager.user == null)
        val user = DatabaseManager.db.getUserById("0").join()
        assertTrue(user == null)
    }

    @Test
    fun checkUserHasAConnectionAfterSignIn() {
        scenario.onActivity { MockAuthenticator(it).signIn().join() }
        val user = DatabaseManager.db.getUserById("0").join()
        assertTrue(user != null)
        assertTrue(user?.connections != null)
        assertTrue(user?.connections?.size!! > 0)
    }

    @Test
    fun checkUserHasAConnectionLessWhenSignOut() {
        scenario.onActivity { MockAuthenticator(it).signIn().join() }
        val user = DatabaseManager.db.getUserById("0").join()
        val size = user?.connections?.size!!
        scenario.onActivity { MockAuthenticator(it).signOut().join() }
        assertTrue(user.connections.size == size-1)
    }
}