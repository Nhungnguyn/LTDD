package com.example.drivingtestapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("questions")
    suspend fun getAllQuestions(): Response<List<Question>>

    @GET("questions/set/{setName}")
    suspend fun getQuestionsBySet(@Path("setName") setName: String): Response<List<Question>>

    @GET("questions/critical")
    suspend fun getCriticalQuestions(): Response<List<Question>>

    @GET("traffic_signs")
    suspend fun getTrafficSigns(): Response<List<TrafficSign>>
}