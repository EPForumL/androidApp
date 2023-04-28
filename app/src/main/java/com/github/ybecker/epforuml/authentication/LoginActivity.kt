package com.github.ybecker.epforuml.authentication

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.MockDatabase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * Activity that shows the login options
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val authenticator = FirebaseAuthenticator(this)

        DatabaseManager.useMockDatabase()

        val signInButton = findViewById<Button>(R.id.signInButton)
        val guestButton = findViewById<Button>(R.id.guestButton)
        signInButton.setOnClickListener {
            signInButton.isEnabled = false
            guestButton.isEnabled = false
            authenticator.signIn().thenAccept {
                signInButton.isEnabled = true
                guestButton.isEnabled = true
            }
        }
        guestButton.setOnClickListener { continueAsGuest() }

        checkIfAlreadySignedIn()
    }

    /**
     * Skips connection to go directly to the main activity
     */
    private fun continueAsGuest() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /**
     * If there is already a user logged in, skip to the main activity
     */
    private fun checkIfAlreadySignedIn() {
        var userId: String? = null
        if (Firebase.auth.currentUser != null) userId = Firebase.auth.currentUser!!.uid
        else if (DatabaseManager.user != null) userId = DatabaseManager.user!!.userId

        if (userId != null) {
            DatabaseManager.db.getUserById(userId).thenAccept {
                DatabaseManager.user = it
                // Skips the login activity if there is already a user logged in
                if (it != null) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}