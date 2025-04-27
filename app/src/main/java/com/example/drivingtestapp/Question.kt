package com.example.drivingtestapp

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    @SerializedName("id") val id: Int,
    @SerializedName("question") val question: String,
    @SerializedName("option_a") val option_a: String,
    @SerializedName("option_b") val option_b: String,
    @SerializedName("option_c") val option_c: String,
    @SerializedName("option_d") val option_d: String,
    @SerializedName("answer") val answer: Int,
    @SerializedName("explanation") val explanation: String?,
    @SerializedName("set_name") val setName: String?,
    @SerializedName("is_critical") val isCritical: Boolean,
    @SerializedName("image_url") val imageUrl: String?
) : Parcelable {
    fun getOptions(): List<String> {
        return listOf(option_a, option_b, option_c, option_d)
    }
}