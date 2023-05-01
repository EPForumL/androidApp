package com.github.ybecker.epforuml.notifications

enum class NotificationType(private val v: String) {
    QUESTION("question"), ANSWER("answer");

    fun getName(): String {
        return v
    }
}