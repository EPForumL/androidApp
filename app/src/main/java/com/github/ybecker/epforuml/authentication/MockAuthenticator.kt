package com.github.ybecker.epforuml.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model

class MockAuthenticator(private val activity: AppCompatActivity) : Authenticator {
    override fun signIn() {
        DatabaseManager.user =
            DatabaseManager.getDatabase().addUser("0", "TestUser")
        activity.startActivity(Intent(activity, MainActivity::class.java))
    }

    override fun signOut() {
        DatabaseManager.user = null
    }

    override fun deleteUser() {
        DatabaseManager.user = null
        // TODO: Remove user from database
    }
}