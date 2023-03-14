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
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.Database
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.android.gms.auth.api.credentials.CredentialsClient
import com.google.firebase.auth.FirebaseAuth

/**
 * Authenticator implementation that uses firebase authentication mechanisms
 */
class FirebaseAuthenticator : Authenticator {
    override var user: Model.User? = null

    override fun signIn(signInLauncher: ActivityResultLauncher<Intent>) {
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

    override fun signOut(activity: AppCompatActivity) {
        if (user != null) {
            AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener {
                    Toast.makeText(activity, "Successfully signed out", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun deleteUser(activity: AppCompatActivity) {
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
}