package com.github.ybecker.epforuml

object DatabaseManager {
    private var db: Database = FirebaseDatabaseAdapter()

    init {
        //as Firebase database is not implemented for now
        useMockDatabase();
    }

    fun getDatabase(): Database? {
        return db
    }

    fun useMockDatabase() {
        db = MockDatabase()
    }
}
