package com.Stylo.stylo.RetrofitApi

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST("login.php")
    fun loginUser(@Body request: LoginRequest): Call<LocalResponse>

    @Headers("Content-Type: application/json")
    @POST("signup.php")
    fun signupUser(@Body request: SignupRequest): Call<LocalResponse>

    @Headers("Content-Type: application/json")
    @POST("FetchProduct.php")
    fun getProducts(): Call<FetchProduct>
}
