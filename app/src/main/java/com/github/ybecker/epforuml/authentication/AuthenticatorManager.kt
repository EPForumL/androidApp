package com.github.ybecker.epforuml.authentication

object AuthenticatorManager {
    private var authenticator = FirebaseAuthenticator()

    fun getAuthenticator(): Authenticator {
        return authenticator
    }
}