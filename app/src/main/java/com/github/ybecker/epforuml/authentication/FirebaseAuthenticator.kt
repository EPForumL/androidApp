package com.github.ybecker.epforuml.authentication

import android.content.ContentValues
import android.content.Intent
import android.util.Log
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
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.account.AccountFragment
import com.github.ybecker.epforuml.account.AccountFragmentGuest
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
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

    // Used to wait for the result to proceed
    private lateinit var signInResult: CompletableFuture<Void>
    private lateinit var signOutResult: CompletableFuture<Void>

    // Will be used to launch the sign in intent
    private val signInLauncher = caller.registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res -> this.onSignInResult(res) }

    override fun signIn(): CompletableFuture<Void> {
        signInResult = CompletableFuture()
        // Adds the authentication means
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .setTheme(
                R.style.Theme_EPForumL
            )
            .build()

        // Shows to the user the authentication means
        signInLauncher.launch(signInIntent)
        return signInResult
    }

    override fun signOut(): CompletableFuture<Void> {
        signOutResult = CompletableFuture()
        if (DatabaseManager.user != null) {
            AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener {
                    logout("Successfully signed out")
                }
        }
        return signOutResult
    }

    override fun deleteUser(): CompletableFuture<Void> {
        signOutResult = CompletableFuture()
        val user = DatabaseManager.user
        if (user != null) {
            AuthUI.getInstance()
                .delete(activity)
                .addOnCompleteListener {
                    DatabaseManager.db.removeUser(user.userId)
                    DatabaseManager.user = null
                    logout("Successfully deleted user : ${user.username}")
                }
        }
        return signOutResult
    }

    /**
     * Logout the user, show guest fragment and show toast message
     *
     * @param txt: The text to show on the toast
     */
    private fun logout(txt: String) {
        DatabaseManager.user?.let { DatabaseManager.db.removeUserConnection(it.userId) }
        // User is logged out
        DatabaseManager.user = null

        // Change to guest fragment
        val fragment = caller as Fragment
        fragment.parentFragmentManager
            .beginTransaction()
            .replace(fragment.id, AccountFragmentGuest())
            .commit()

        signOutResult.complete(null)
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
        signInResult.complete(null)
    }

    /**
     * Adds logged user to the database and starts MainActivity or AccountFragment
     */
    private fun signInSucceeds() {
        val firebaseUser = Firebase.auth.currentUser
        if (firebaseUser != null) {
            DatabaseManager.db.getUserById(firebaseUser.uid).thenAccept { user ->
                if (user == null) {
                    DatabaseManager.db.addUser(
                        firebaseUser.uid,
                        firebaseUser.displayName!!,
                        firebaseUser.email!!
                    ).thenAccept { newUser ->
                        gotToActivity(newUser)
                    }
                } else {
                    gotToActivity(user)
                }
            }
        }
    }

    /**
     * Shows sign-in Toast and goes to MainActivity or AccountFragment
     */
    private fun gotToActivity(newUser: Model.User) {
        DatabaseManager.user = newUser
        DatabaseManager.db.setUserPresence(DatabaseManager.user!!.userId)
        DatabaseManager.user!!.connections.add(true)

        Toast.makeText(
            activity,
            "Successfully signed in as ${newUser.username}",
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