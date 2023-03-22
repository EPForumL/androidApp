package com.github.ybecker.epforuml.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model

class MockAuthenticator(private val activity: AppCompatActivity) : Authenticator {
    override var user: Model.User? = null

    override fun signIn() {
        user = DatabaseManager.getDatabase().addUser("id", "Un BG sans nom").get()
        activity.startActivity(Intent(activity, MainActivity::class.java))
    }

    override fun signOut() {
        user = null
    }

    override fun deleteUser() {
        user = null
        // TODO: Remove user from database
    }
}