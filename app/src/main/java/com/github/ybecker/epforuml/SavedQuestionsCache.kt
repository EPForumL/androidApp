package com.github.ybecker.epforuml

import com.github.ybecker.epforuml.database.Model

class SavedQuestionsCache : Cache {
    private var cache = HashMap<String, Model.Question>()

    override val size: Int
        get() = cache.size

    override fun set(key: String, value: Model.Question) {
        cache[key] = value
    }

    override fun get(key: String): Model.Question? {
        return cache[key]
    }

    override fun remove(key: String): Model.Question? {
        return cache.remove(key)
    }

    override fun clear() {
        cache.clear()
    }

    fun toList() : MutableList<Model.Question> {
        var list = mutableListOf<Model.Question>()
        for ((k, v) in cache) {
            list.add(v)
        }

        return list
    }
}


interface Cache {
    val size: Int

    operator fun set(key: String, value: Model.Question)

    operator fun get(key: String): Model.Question?

    fun remove(key: String): Model.Question?

    fun clear()
}