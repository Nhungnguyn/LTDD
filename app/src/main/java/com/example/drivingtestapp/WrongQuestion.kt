package com.example.drivingtestapp

data class WrongQuestion(
    val question: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctAnswer: Int = -1,
    val userSelected: Int = -1,
    val timestamp: Long = 0L
)
