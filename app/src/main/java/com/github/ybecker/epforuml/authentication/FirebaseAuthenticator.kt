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
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

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
        // If the user is already connected set the user to the corresponding value
        val firebaseUser = Firebase.auth.currentUser
        if (firebaseUser != null) {
            DatabaseManager.user =
                Model.User(
                    firebaseUser.uid,
                    firebaseUser.displayName ?: "",
                    firebaseUser.email ?: ""
                )
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
                    logout("Successfully signed out")
                }
        }
    }

    override fun deleteUser() {
        val user = DatabaseManager.user
        if (user != null) {
            AuthUI.getInstance()
                .delete(activity)
                .addOnCompleteListener {
                    // TODO: Remove user from database
                    DatabaseManager.user = null
                    logout("Successfully deleted user : ${user.username}")
                }
        }
    }

    /**
     * Logout the user, show guest fragment and show toast message
     *
     * @param txt: The text to show on the toast
     */
    private fun logout(txt: String) {
        // User is logged out
        DatabaseManager.user = null

        // Change to guest fragment
        val fragment = caller as Fragment
        fragment.parentFragmentManager
            .beginTransaction()
            .replace(fragment.id, AccountFragmentGuest())
            .commit()

        Toast.makeText(activity, txt, Toast.LENGTH_LONG).show()
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

    /**
     * Adds logged user to the database and starts MainActivity or AccountFragment
     */
    private fun signInSucceeds() {
        val firebaseUser = Firebase.auth.currentUser
        if (firebaseUser != null) {
            val username = firebaseUser.displayName
            username?.let {
                // Adds user to the database
                val user = DatabaseManager.user
                if (user == null || user.userId != firebaseUser.uid) {
                    val futureUser = DatabaseManager.db.addUser(firebaseUser.uid, it, firebaseUser.email ?: "")
                    futureUser.thenAccept { user -> DatabaseManager.user = user }
                }
            }

            Toast.makeText(
                activity,
                "Successfully signed in as $username",
                Toast.LENGTH_LONG
            ).show()

            // If this is the login activity go to main otherwise switch from guest fragment to
            // account fragment.
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