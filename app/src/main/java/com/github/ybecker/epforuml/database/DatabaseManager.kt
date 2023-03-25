package com.github.ybecker.epforuml.database

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object DatabaseManager {
    var db: Database = FirebaseDatabaseAdapter()

    private val firebaseUser = Firebase.auth.currentUser
    var user: Model.User? = firebaseUser?.uid?.let {id ->
        firebaseUser.displayName?.let { name ->
            Model.User(
                id,
                name,
                listOf(),
                listOf(),
                listOf()
            )
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
