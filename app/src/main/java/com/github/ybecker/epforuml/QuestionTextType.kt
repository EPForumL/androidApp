package com.github.ybecker.epforuml

enum class QuestionTextType(private val v: String) {
    TEXT("text"), LATEX("latex");

    fun getName(): String {
        return v
    }
}
