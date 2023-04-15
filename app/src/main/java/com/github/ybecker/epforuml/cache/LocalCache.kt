package com.github.ybecker.epforuml.cache

import android.app.Application

class LocalCache : Application() {
    var savedQuestionsCache = SavedQuestionsCache()

    fun getSavedQuestions() : SavedQuestionsCache {
        return savedQuestionsCache
    }
}