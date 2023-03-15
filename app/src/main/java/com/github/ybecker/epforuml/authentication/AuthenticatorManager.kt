package com.github.ybecker.epforuml.authentication

/**
 * Object used to access an authenticator (only FirebaseAuthenticator for now)
 */
object AuthenticatorManager {
    private var authenticator = FirebaseAuthenticator()

    /**
     * Gives the authenticator
     *
     * @return the authenticator
     */
    fun getAuthenticator(): Authenticator {
        return authenticator
    }
}