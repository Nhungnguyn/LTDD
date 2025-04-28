package com.example.drivingtestapp

import android.os.Build
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object RetrofitClient {

    private const val PORT = 3000
    private const val LOCAL_IP = "10.0.2.2"       // IP cho Emulator
    private const val HOST_IP = "192.168.1.9"    // IP máy tính trong mạng LAN (Đã sửa, KHÔNG thừa space)

    private val BASE_URL: String
        get() {
            val isEmulator = (Build.FINGERPRINT.contains("generic") ||
                    Build.MODEL.contains("google_sdk") ||
                    Build.MODEL.contains("Emulator") ||
                    Build.MODEL.contains("Android SDK built for x86"))
            return if (isEmulator) {
                "http://$LOCAL_IP:$PORT/"
            } else {
                "http://$HOST_IP:$PORT/"
            }
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
