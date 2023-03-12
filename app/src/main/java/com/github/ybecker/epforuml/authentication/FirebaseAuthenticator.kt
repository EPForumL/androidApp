package com.github.ybecker.epforuml.authentication

import android.content.Intent
import android.net.Credentials
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.Database
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.android.gms.auth.api.credentials.CredentialsClient
import com.google.firebase.auth.FirebaseAuth

/**
 * Authenticator implementation that uses firebase authentication mechanisms
 */
class FirebaseAuthenticator(private val activity: AppCompatActivity) : Authenticator {
    private val signInLauncher: ActivityResultLauncher<Intent>
    private val database: Database

    private var user: Model.User?

    init {
        signInLauncher = activity.registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { res -> this.onSignInResult(res) }

        database = DatabaseManager.getDatabase()

        user = null
    }

    override fun signIn() {
        // Adds the authentication means
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()

        // Shows to the user the authentication means
        signInLauncher.launch(signInIntent)
    }

    override fun signOut() {
        if (user != null) {
            AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener {
                    makeAndShowToast("Successfully signed out")
                } // TODO: Do something more when logout ?
        }
    }

    override fun deleteUser() {
        if (user != null) {
            AuthUI.getInstance()
                .delete(activity)
                .addOnCompleteListener {
                    // TODO: Remove user from database
                    makeAndShowToast("Successfully deleted user : ${user!!.username}")
                }
        }
    }

    override fun getUser(): Model.User? {
        return user
    }

    /**
     * Simple method to create and show a Toast with the given string
     *
     * @param txt: The string to be shown in the Toast
     */
    private fun makeAndShowToast(txt: String) {
        Toast.makeText(activity, txt, Toast.LENGTH_LONG).show()
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult?) {
        val response = result?.idpResponse
        if (result?.resultCode == AppCompatActivity.RESULT_OK) {
            // Successfully signed in
            val firebaseUser = FirebaseAuth.getInstance().currentUser

            if (firebaseUser != null) {
                val username = firebaseUser.displayName
                username?.let {
                    user = Model.User(firebaseUser.uid, it, listOf(), listOf())
                    database.addUser(firebaseUser.uid, it)
                }

                makeAndShowToast("Successfully signed in as $username")
                // TODO: Go to next activity
            }
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button.
            if (response != null) {
                val error = response.error?.errorCode
                makeAndShowToast("Could not login : ${error.toString()}")
            } else {
                makeAndShowToast("Canceled authentication")
            }
        }
    }
}