package com.Stylo.stylo.RetrofitApi

import com.Stylo.stylo.Utils.ConstantUrl.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {  // Replace with your API URL

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}
