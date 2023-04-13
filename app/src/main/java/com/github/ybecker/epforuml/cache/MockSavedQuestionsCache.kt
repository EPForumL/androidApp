package com.github.ybecker.epforuml.cache

import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model

class MockSavedQuestionsCache {
    private var mockCache = SavedQuestionsCache()

    init {
        DatabaseManager.useMockDatabase()

        db.getQuestionById("question1").thenAccept {
            mockCache.set("question1", it!!)
        }
    }

    fun toList() : MutableList<Model.Question> {
        return mockCache.toList()
    }
}