package com.github.ybecker.epforuml.database

object DatabaseManager {
    var db: Database = FirebaseDatabaseAdapter()

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
