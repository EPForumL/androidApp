package com.github.ybecker.epforuml.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.util.EspressoIdlingResource
import java.util.concurrent.CompletableFuture

class MockAuthenticator(private val activity: AppCompatActivity) : Authenticator {
    private val userId = "0"
    private val userName = "TestUser"
    private val email = "testEmail"
    private lateinit var signInResult: CompletableFuture<Void>
    private lateinit var signOutResult: CompletableFuture<Void>

    override fun signIn(): CompletableFuture<Void> {
        EspressoIdlingResource.increment()

        signInResult = CompletableFuture()
        val futureUser =
            DatabaseManager.getDatabase().addUser(userId, userName, email)
        futureUser.thenAccept {
            DatabaseManager.user = it
            DatabaseManager.db.setUserPresence(it.userId)
            DatabaseManager.user!!.connections.add(true)

            activity.startActivity(Intent(activity, MainActivity::class.java))

            signInResult.complete(null)

            EspressoIdlingResource.decrement()
        }
        return signInResult
    }

    override fun signOut(): CompletableFuture<Void> {
        return signOutOrDelete { DatabaseManager.db.removeUserConnection(it) }
    }

    override fun deleteUser(): CompletableFuture<Void> {
        return signOutOrDelete { DatabaseManager.db.removeUser(it) }
    }

    private fun signOutOrDelete(execute: (userId: String) -> Unit): CompletableFuture<Void> {
        EspressoIdlingResource.increment()

        signOutResult = CompletableFuture()
        val user = DatabaseManager.user
        if (user != null) {
            execute(user.userId)
            DatabaseManager.user = null
            signOutResult.complete(null)

            EspressoIdlingResource.decrement()
        }
        return signOutResult
    }
}