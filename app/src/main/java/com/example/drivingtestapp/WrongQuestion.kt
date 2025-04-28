package com.example.drivingtestapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WrongQuestion(
    val questionId: Int = -1,
    val question: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctAnswer: Int = -1,
    val userSelected: Int = -1,
    val timestamp: Long = 0L,
    val wrongCount: Int = 0
) : Parcelable