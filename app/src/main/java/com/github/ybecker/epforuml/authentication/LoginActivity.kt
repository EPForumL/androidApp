package com.github.ybecker.epforuml.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager

/**
 * Activity that shows the login options
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // TODO: Use real database when available
        DatabaseManager.useMockDatabase()

        AuthenticatorManager.createFirebaseAuthenticator(this)
        val authenticator = AuthenticatorManager.authenticator

        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener { authenticator?.signIn() }

        val guestButton = findViewById<Button>(R.id.guestButton)
        guestButton.setOnClickListener { continueAsGuest() }
    }

    /**
     * Skips connection to go directly to the main activity
     */
    private fun continueAsGuest() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}