package com.Stylo.stylo.RetrofitApi

import com.Stylo.stylo.Utils.ConstantUrl.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 🛠 Logs full request & response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // 🔥 Attach logger here
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}
