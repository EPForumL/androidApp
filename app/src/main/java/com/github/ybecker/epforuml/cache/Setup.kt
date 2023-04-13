package com.github.ybecker.epforuml.cache

import android.app.Application
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model

class Setup : Application() {
    private lateinit var user : Model.User

    override fun onCreate() {
        super.onCreate()

        user = DatabaseManager.user ?: Model.User()

        db.getInstance().setPersistenceEnabled(true)

    }
}