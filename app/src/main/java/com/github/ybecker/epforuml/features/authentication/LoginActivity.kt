package com.github.ybecker.epforuml.features.authentication



import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.ybecker.epforuml.util.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Activity that shows the login options
 */
class LoginActivity : AppCompatActivity() {

    companion object {
        lateinit var loginContext : Context

        /**
         * Saved question cache and answer cache to device
         */
        fun saveUserToDevice(user: Model.User?) {
            val sharedUser : SharedPreferences = loginContext.getSharedPreferences("USER", MODE_PRIVATE)
            val userEditor = sharedUser.edit()

            val uGson = Gson()

            val uJson = uGson.toJson(user)

            userEditor.putString("user", uJson)
            userEditor.apply()
        }
    }

    private var savedUser : Model.User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginContext = applicationContext

        loadUserIfAny()

        val authenticator = FirebaseAuthenticator(this)

        val signInButton = findViewById<Button>(R.id.signInButton)
        val guestButton = findViewById<Button>(R.id.guestButton)
        signInButton.setOnClickListener {
            signInButton.isEnabled = false
            guestButton.isEnabled = false
            authenticator.signIn().thenAccept {
                signInButton.isEnabled = true
                guestButton.isEnabled = true
            }
        }
        guestButton.setOnClickListener { continueAsGuest() }

        checkIfAlreadySignedIn()
    }

    /**
     * Helper function for function below
     */
    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)


    private fun loadUserIfAny() {
        val sharedUser : SharedPreferences = loginContext.getSharedPreferences("USER", MODE_PRIVATE)
        val uGson = Gson()

        val uJson =  sharedUser.getString("user", null)

        if (uJson != null) {
            val userTmp = uGson.fromJson<Model.User>(uJson)
            if (userTmp != null) {
                savedUser = userTmp
            }
        }
    }

    /**
     * Skips connection to go directly to the main activity
     */
    private fun continueAsGuest() {
        startActivity(Intent(this, MainActivity::class.java))
        saveUserToDevice(null)
        finish()
    }

    /**
     * If there is already a user logged in, skip to the main activity
     */
    private fun checkIfAlreadySignedIn() {

        var userId: String? = null

        if (MainActivity.isConnected()) {
            if (Firebase.auth.currentUser != null) userId = Firebase.auth.currentUser!!.uid
            else if (DatabaseManager.user != null) userId = DatabaseManager.user!!.userId

            if (userId != null) {
                DatabaseManager.db.getUserById(userId).thenAccept {
                    DatabaseManager.user = it
                    // Skips the login activity if there is already a user logged in
                    if (it != null) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }
        } else if (savedUser != null) {
            DatabaseManager.user = savedUser
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        /*
        var userId: String? = null
        if (Firebase.auth.currentUser != null) userId = Firebase.auth.currentUser!!.uid
        else if (DatabaseManager.user != null) userId = DatabaseManager.user!!.userId

        if (userId != null) {
            DatabaseManager.db.getUserById(userId).thenAccept {
                DatabaseManager.user = it
                // Skips the login activity if there is already a user logged in
                if (it != null) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }

         */
    }
}