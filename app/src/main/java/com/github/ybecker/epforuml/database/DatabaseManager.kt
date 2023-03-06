package com.github.ybecker.epforuml.database

object DatabaseManager {
    private var db: Database = FirebaseDatabaseAdapter()

    fun getDatabase(): Database {
        return db
    }

    fun useMockDatabase() {
        db = MockDatabase()
    }
}
