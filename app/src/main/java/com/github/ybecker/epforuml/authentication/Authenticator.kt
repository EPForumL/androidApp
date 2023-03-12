package com.github.ybecker.epforuml.authentication

import com.github.ybecker.epforuml.database.Model
import com.google.firebase.auth.FirebaseUser

/**
 * Interface that represents a mean to authenticate
 */
interface Authenticator {
    /**
     * Allows a user to sign-in
     */
    fun signIn()

    /**
     * Allows the current logged-in user to sign-out
     */
    fun signOut()

    /**
     * Deletes the current logged-in user from the firebase user list and the database
     */
    fun deleteUser()

    /**
     * Returns the current logged-in user
     *
     * @return the current logged-in user as a Model.User
     */
    fun getUser(): Model.User?
}