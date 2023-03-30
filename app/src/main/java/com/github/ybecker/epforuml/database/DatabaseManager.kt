package com.github.ybecker.epforuml.database

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object DatabaseManager {
    var db: Database = FirebaseDatabaseAdapter()
    var user: Model.User? = null

    init {
        val firebaseUser = Firebase.auth.currentUser
        if (firebaseUser != null) {
            db.getUserById(firebaseUser.uid).thenAccept { user = it }
        }
    }

    /**
     * Retrieves the current instance of the Database
     *
     * @return the current instance of the Database
     */
    fun getDatabase(): Database {
        return db
    }

    /**
     * Modify the current instance of the Database to a MockDatabase
     */
    fun useMockDatabase() {
        db = MockDatabase()
    }
}
