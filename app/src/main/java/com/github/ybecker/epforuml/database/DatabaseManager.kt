package com.github.ybecker.epforuml.database

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object DatabaseManager {
    var db: Database = FirebaseDatabaseAdapter(Firebase.database)

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
