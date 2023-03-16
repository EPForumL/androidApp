package com.github.ybecker.epforuml.authentication

import android.content.Intent
import android.net.Credentials
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.Database
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.android.gms.auth.api.credentials.CredentialsClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Authenticator implementation that uses firebase authentication mechanisms
 */
class FirebaseAuthenticator(private val activity: AppCompatActivity) : Authenticator {
    override var user: Model.User? = null

    // Will be used to launch the sign in intent
    private val signInLauncher = activity.registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res -> this.onSignInResult(res) }

    override fun signIn() {
        // Adds the authentication means
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        // Shows to the user the authentication means
        signInLauncher.launch(signInIntent)
    }

    override fun signOut() {
        if (user != null) {
            AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener {
                    Toast.makeText(activity, "Successfully signed out", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun deleteUser() {
        if (user != null) {
            AuthUI.getInstance()
                .delete(activity)
                .addOnCompleteListener {
                    // TODO: Remove user from database
                    Toast.makeText(
                        activity,
                        "Successfully deleted user : ${user!!.username}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    /**
     * Depending on the result, adds the user to the database and proceeds to the next activity
     * if authentication was successful or shows an error message if it wasn't.
     *
     * @param result: The result of the authentication
     */
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult?) {
        val response = result?.idpResponse
        if (result?.resultCode == AppCompatActivity.RESULT_OK) {
            // Successfully signed in
            signInSucceeds(FirebaseAuth.getInstance().currentUser)
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button.
            if (response != null) {
                val error = response.error?.errorCode
                print("Could not login : ${error.toString()}")
            } else {
                Toast.makeText(
                    activity,
                    "Canceled authentication",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun signInSucceeds(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null) {
            val username = firebaseUser.displayName
            username?.let {
                AuthenticatorManager.authenticator?.user =
                    DatabaseManager.getDatabase().addUser(firebaseUser.uid, it)
            }

            Toast.makeText(
                activity,
                "Successfully signed in as $username",
                Toast.LENGTH_LONG
            ).show()

            activity.startActivity(Intent(activity, MainActivity::class.java))
        }
    }
}