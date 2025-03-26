package com.Stylo.stylo.RetrofitApi

import com.Stylo.stylo.Utils.ConstantUrl
import com.Stylo.stylo.data.model.Product
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import kotlin.jvm.java

interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST("login.php")
    fun loginUser(@Body request: LoginRequest): Call<LocalResponse>

    @Headers("Content-Type: application/json")
    @POST("signup.php")
    fun signupUser(@Body request: SignupRequest): Call<LocalResponse>

//    @FormUrlEncoded
//    @POST("fetchproducts.php")
//    suspend fun fetchProducts(
//        @Field("subcategoryid") subcategoryid: String?,
//    ): Response<GetProductData>

}
