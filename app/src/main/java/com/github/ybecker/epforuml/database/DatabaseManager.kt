package com.github.ybecker.epforuml.database

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import com.google.firebase.auth.ktx.auth
import java.util.concurrent.CompletableFuture

object DatabaseManager {
    var db: Database = FirebaseDatabaseAdapter(Firebase.database)
    var user: Model.User? = null

    val anonymousUsers =
        listOf("The Incognito Engineer",
        "Stealthy Scholar",
        "Mysterious Mechanic",
        "The Invisible Innovator",
        "Secret Sapper",
        "The Hidden Hacker",
        "Masked Maven",
        "The Anonymous Analyst",
        "The Covert Coder",
        "Phantom Programmer",
        "The Elusive Engineer",
        "Shadowy Scientist",
        "Camouflaged Creator",
        "The Veiled Virtuoso",
        "Anonymous Alchemist",
        "The Cryptic Craftsman",
        "Silent Scientist",
        "The Shrouded Savant",
        "The Unknown Engineer",
        "The Clandestine Constructor",
        "The Invisible Inventor",
        "Anonymous Artificer",
        "The Sneaky Scientist",
        "The Phantom Philosopher",
        "The Enigmatic Engineer",
        "Mysterious Maker",
        "The Concealed Craftsman",
        "The Ghostly Genius",
        "The Secret Schemer",
        "The Unknown Creator")

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
