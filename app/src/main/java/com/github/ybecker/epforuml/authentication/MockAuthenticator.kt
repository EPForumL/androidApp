package com.github.ybecker.epforuml.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model

class MockAuthenticator(private val activity: AppCompatActivity) : Authenticator {
    private val userId = "0"
    private val userName = "TestUser"
    private val email = "testEmail"

    override fun signIn() {
        val futureUser =
            DatabaseManager.getDatabase().addUser(userId, userName, email)
        futureUser.thenAccept {
            DatabaseManager.user = it
            DatabaseManager.db.setUserPresence(it.userId)
            DatabaseManager.user!!.connections.add(true)
            activity.startActivity(Intent(activity, MainActivity::class.java))
        }
    }

    override fun signOut() {
        val user = DatabaseManager.user
        if (user != null) {
            DatabaseManager.db.removeUserConnection(user.userId)
            DatabaseManager.user = null
        }
    }

    override fun deleteUser() {
        val user = DatabaseManager.user
        if (user != null) {
            DatabaseManager.db.removeUser(user.userId)
            DatabaseManager.user = null
        }
    }
}