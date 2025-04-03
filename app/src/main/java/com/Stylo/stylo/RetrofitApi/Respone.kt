package com.Stylo.stylo.RetrofitApi

import com.google.gson.annotations.SerializedName

data class LocalResponse(
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: Boolean
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class SignupRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
