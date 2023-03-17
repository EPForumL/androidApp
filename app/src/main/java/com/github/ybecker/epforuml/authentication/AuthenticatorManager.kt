package com.github.ybecker.epforuml.authentication

import androidx.appcompat.app.AppCompatActivity

/**
 * Object used to access an authenticator (only FirebaseAuthenticator for now)
 */
object AuthenticatorManager {
    var authenticator: Authenticator? = null

    /**
     * Creates and uses the firebase authenticator when the activity is known
     */
    fun createFirebaseAuthenticator(activity: AppCompatActivity) {
        authenticator = FirebaseAuthenticator(activity)
    }

    /**
     * Creates and uses the mock authenticator when the activity is known
     */
    fun createMockAuthenticator(activity: AppCompatActivity) {
        authenticator = MockAuthenticator(activity)
    }
}