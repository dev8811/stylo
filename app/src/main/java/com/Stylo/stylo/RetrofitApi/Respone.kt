package com.Stylo.stylo.RetrofitApi

data class LocalResponse(
    val message: String,
    val status: Boolean
)

data class LoginRequest(
    val email: String,
    val password: String
)
data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)
data class FetchProduct(
    val products: List<Product>,
    val status: Boolean
)
data class Product(
    val categoryid: String,
    val description: String,
    val discount: String,
    val originalprice: String,
    val productid: String,
    val productimage: String,
    val productname: String,
    val rating: String,
    val review: String
)