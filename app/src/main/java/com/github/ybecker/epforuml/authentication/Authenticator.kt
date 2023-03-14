package com.github.ybecker.epforuml.authentication

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.auth.FirebaseUser

/**
 * Interface that represents a mean to authenticate
 */
interface Authenticator {
    var user: Model.User?
    /**
     * Allows a user to sign-in
     */
    fun signIn(signInLauncher: ActivityResultLauncher<Intent>)

    /**
     * Allows the current logged-in user to sign-out
     */
    fun signOut(activity: AppCompatActivity)

    /**
     * Deletes the current logged-in user from the firebase user list and the database
     */
    fun deleteUser(activity: AppCompatActivity)
}