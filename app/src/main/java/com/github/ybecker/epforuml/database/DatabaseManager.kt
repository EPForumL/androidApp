package com.github.ybecker.epforuml.database

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import com.google.firebase.auth.ktx.auth
import java.util.concurrent.CompletableFuture

object DatabaseManager {
    var db: Database = FirebaseDatabaseAdapter(Firebase.database)
    var user: Model.User? = null

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
