package com.github.ybecker.epforuml.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // TODO: Use real database when available
        DatabaseManager.useMockDatabase()
        val authenticator = AuthenticatorManager.getAuthenticator()

        val signInLauncher = registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { res -> this.onSignInResult(res) }

        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener { authenticator.signIn(signInLauncher) }

        val guestButton = findViewById<Button>(R.id.guestButton)
        guestButton.setOnClickListener { continueAsGuest() }
    }

    private fun continueAsGuest() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    /**
     * Depending on the result, adds the user to the database and proceeds to the next activity
     * if authentication was successful or shows an error message if it wasn't.
     *
     * @param result: The result of the authentication
     */
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult?) {
        val response = result?.idpResponse
        if (result?.resultCode == RESULT_OK) {
            // Successfully signed in
            val firebaseUser = FirebaseAuth.getInstance().currentUser

            if (firebaseUser != null) {
                val username = firebaseUser.displayName
                username?.let {
                    AuthenticatorManager.getAuthenticator().user =
                        Model.User(firebaseUser.uid, it, listOf(), listOf(), listOf())
                    DatabaseManager.getDatabase().addUser(firebaseUser.uid, it)
                }

                Toast.makeText(
                    this,
                    "Successfully signed in as $username",
                    Toast.LENGTH_LONG
                ).show()

                startActivity(Intent(this, MainActivity::class.java))
            }
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button.
            var errorTxt = ""
            if (response != null) {
                val error = response.error?.errorCode
                errorTxt = "Could not login : ${error.toString()}"
            } else {
                errorTxt = "Canceled authentication"
            }

            Toast.makeText(
                this,
                errorTxt,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}