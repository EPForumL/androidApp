package com.github.ybecker.epforuml.cache

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import junit.framework.TestCase.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SavedQuestionsCacheTest {
    private var cache = SavedQuestionsCache()

    private lateinit var question1 : Model.Question
    private val ID1 = "question1"

    private lateinit var question2 : Model.Question
    private val ID2 = "question2"

    @Before
    fun setup() {
        DatabaseManager.useMockDatabase()

        db.getQuestionById(ID1).thenAccept {
            question1 = it!!
        }

        db.getQuestionById(ID2).thenAccept {
            question2 = it!!
        }

        cache.clear()
    }

    @Test
    fun clearIsEmpty() {
        assertTrue(cache.toListOfQuestions().isEmpty())
    }

    @Test
    fun addingElementContainsElement() {
        cache.set(ID1, question1)

        assertEquals(question1.questionId, cache.get(ID1)?.questionId)
    }

    @Test
    fun toListContainsElements() {
        cache.set(ID1, question1)
        cache.set(ID2, question2)

        val list = cache.toListOfQuestions()

        assertTrue(list.contains(question1))
        assertTrue(list.contains(question2))
    }

    @Test
    fun emptyCacheReturnsNotSaved() {
        assertFalse(cache.isQuestionSaved(ID2))
        assertFalse(cache.isQuestionSaved(ID1))

    }

    @Test
    fun cacheWithElementReturnsSaved() {
        cache.set(ID1, question1)
        assertTrue(cache.isQuestionSaved(ID1))
        assertFalse(cache.isQuestionSaved(ID2))
    }

}