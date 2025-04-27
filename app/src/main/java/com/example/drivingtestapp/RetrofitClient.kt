package com.example.drivingtestapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object RetrofitClient {

    private const val LOCAL_IP = "192.168.1.12"
    private const val PORT = 3000

    private val BASE_URL: String
        get() = if (BuildConfig.DEBUG) {
            "http://10.0.2.2:$PORT/"
        } else {
            "http://$LOCAL_IP:$PORT/"
        }

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
        .create()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
