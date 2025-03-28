package com.Stylo.stylo.RetrofitApi

data class LocalResponse(
    val message: String, val status: Boolean
)

data class LoginRequest(
    val email: String, val password: String
)

data class SignupRequest(
    val name: String, val email: String, val password: String
)

