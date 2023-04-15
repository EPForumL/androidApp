package com.github.ybecker.epforuml.notifications

import android.content.ContentValues
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class TokenUpdateService:  FirebaseMessagingService(){

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(ContentValues.TAG, "Refreshed token: $token")

        //TODO
        // Enregistrement du token dans la base de données
        // ...

        // Envoi du token au serveur d'application si nécessaire
        // ...
    }
}