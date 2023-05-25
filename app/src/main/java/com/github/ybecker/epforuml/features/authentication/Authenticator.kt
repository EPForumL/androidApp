package com.github.ybecker.epforuml.features.authentication

import java.util.concurrent.CompletableFuture

/**
 * Interface that represents a mean to authenticate
 */
interface Authenticator {
    /**
     * Allows a user to sign-in
     */
    fun signIn(): CompletableFuture<Void>

    /**
     * Allows the current logged-in user to sign-out
     */
    fun signOut(): CompletableFuture<Void>

    /**
     * Deletes the current logged-in user from the firebase user list and the database
     */
    fun deleteUser(): CompletableFuture<Void>
}