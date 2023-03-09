package com.github.ybecker.epforuml.authentication

import com.github.ybecker.epforuml.R

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.firebase.ui.auth.AuthUI

import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.firebase.auth.FirebaseAuth

class FirebaseLoginActivity : AppCompatActivity() {
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_login)

        // TODO: Combine with Luna's login UI
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener { this.signIn() }

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener { this.signOut() }
    }

    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun signOut() {
        val loginResult = findViewById<TextView>(R.id.loginResult)
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener { loginResult.text = "Successfully signed out" } // TODO: Do something when logout
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult?) {
        val response = result?.idpResponse
        val loginResult = findViewById<TextView>(R.id.loginResult)
        if (result?.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                // TODO: Use real database instead
                DatabaseManager.useMockDatabase()
                val db = DatabaseManager.getDatabase()
                val username = user.displayName
                username?.let { db.addUser(user.uid, it) }

                loginResult.text = "Successfully signed in as $username"
                // TODO: Go to next activity
            }
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button.
            if (response != null) { // TODO: Check error code
                val error = response.error?.errorCode
                loginResult.text = "Could not login : ${error.toString()}"
            } else {
                loginResult.text = "Canceled authentication"
            }
        }
    }
}