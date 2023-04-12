package com.github.ybecker.epforuml.cache

import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model

class MockSavedQuestionsCache : Cache {
    private var mockCache = HashMap<String, Model.Question>()

    override val size: Int
        get() = mockCache.size

    init {
        db.getQuestionById("question1").thenAccept {
            mockCache["question1"] = it!!
        }
    }

    fun toList() : MutableList<Model.Question> {
        var list = mutableListOf<Model.Question>()
        for ((k, v) in mockCache) {
            list.add(v)
        }

        return list
    }
}