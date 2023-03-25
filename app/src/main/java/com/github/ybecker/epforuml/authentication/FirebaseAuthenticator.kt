package com.github.ybecker.epforuml.authentication

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.account.AccountFragment
import com.github.ybecker.epforuml.account.AccountFragmentGuest
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Authenticator implementation that uses firebase authentication mechanisms
 */
class FirebaseAuthenticator(
    private val activity: FragmentActivity,
    private val caller: ActivityResultCaller = activity
    ) : Authenticator {

    // Will be used to launch the sign in intent
    private val signInLauncher = caller.registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res -> this.onSignInResult(res) }

    init {
        val firebaseUser = Firebase.auth.currentUser
        if (firebaseUser != null) {
            DatabaseManager.user =
                firebaseUser.displayName?.let {
                Model.User(firebaseUser.uid,
                    it,
                    listOf(),
                    listOf(),
                    listOf())
            }
        }
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
            .build()

        // Shows to the user the authentication means
        signInLauncher.launch(signInIntent)
    }

    override fun signOut() {
        if (DatabaseManager.user != null) {
            AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener {
                    DatabaseManager.user = null
                    val fragment = caller as Fragment
                    fragment.parentFragmentManager
                        .beginTransaction()
                        .replace(fragment.id, AccountFragmentGuest())
                        .commit()

                    Toast.makeText(
                        activity,
                        "Successfully signed out",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    override fun deleteUser() {
        val user = DatabaseManager.user
        if (user != null) {
            AuthUI.getInstance()
                .delete(activity)
                .addOnCompleteListener {
                    DatabaseManager.user = null
                    // TODO: Remove user from database

                    val fragment = caller as Fragment
                    fragment.parentFragmentManager
                        .beginTransaction()
                        .replace(fragment.id, AccountFragmentGuest())
                        .commit()

                    Toast.makeText(
                        activity,
                        "Successfully deleted user : ${user.username}",
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
            signInSucceeds()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button.
            if (response != null) {
                val error = response.error?.errorCode
                print("\nCould not login : ${error.toString()}\n")
            } else {
                Toast.makeText(
                    activity,
                    "Canceled authentication",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun signInSucceeds() {
        val firebaseUser = Firebase.auth.currentUser
        if (firebaseUser != null) {
            val username = firebaseUser.displayName
            username?.let {
                val user = DatabaseManager.user
                if (user == null || user.userId != firebaseUser.uid)
                    DatabaseManager.user =
                        DatabaseManager.db.addUser(firebaseUser.uid, it)
            }

            Toast.makeText(
                activity,
                "Successfully signed in as $username",
                Toast.LENGTH_LONG
            ).show()

            if (activity::class.java == LoginActivity::class.java) {
                activity.startActivity(Intent(activity, MainActivity::class.java))
                activity.finish()
            } else {
                val fragment = caller as Fragment
                fragment.parentFragmentManager
                    .beginTransaction()
                    .replace(fragment.id, AccountFragment())
                    .commit()
            }
        }
    }
}