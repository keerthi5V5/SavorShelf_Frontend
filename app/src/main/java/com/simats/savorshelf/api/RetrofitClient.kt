package com.simats.savorshelf.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Note: 10.0.2.2 is the localhost loopback for Android Emulators. 
    // For a physical device on the same Wi-Fi, use your computer's IPv4 address from ipconfig.
    private const val BASE_URL = "http://180.235.121.253:8121/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
