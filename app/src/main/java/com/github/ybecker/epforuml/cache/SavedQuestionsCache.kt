package com.github.ybecker.epforuml.cache

import android.os.Parcel
import android.os.Parcelable
import com.github.ybecker.epforuml.database.Model

class SavedQuestionsCache() : Cache, Parcelable {
    private var cache : HashMap<String, Model.Question> = hashMapOf()

    override val size: Int
        get() = cache.size

    constructor(parcel: Parcel) : this() {}

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

    override fun toListOfQuestions() : MutableList<Model.Question> {
        var list = mutableListOf<Model.Question>()
        for ((k, v) in cache) {
            list.add(v)
        }

        return list
    }

    override fun isQuestionSaved(key: String): Boolean {
        val question = get(key)
        if (question != null)
            return true

        return false
    }

    override fun isEmpty(): Boolean {
        return cache.isEmpty()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SavedQuestionsCache> {
        override fun createFromParcel(parcel: Parcel): SavedQuestionsCache {
            return SavedQuestionsCache(parcel)
        }

        override fun newArray(size: Int): Array<SavedQuestionsCache?> {
            return arrayOfNulls(size)
        }
    }


}


interface Cache {
    val size: Int

    operator fun set(key: String, value: Model.Question)

    operator fun get(key: String): Model.Question?

    fun remove(key: String): Model.Question?

    fun clear()

    fun toListOfQuestions(): MutableList<Model.Question>

    fun isQuestionSaved(key: String): Boolean

    fun isEmpty() : Boolean
}