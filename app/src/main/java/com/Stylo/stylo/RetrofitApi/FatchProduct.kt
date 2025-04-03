package com.Stylo.stylo.RetrofitApi

data class FatchProduct(
    val message: String,
    val products: List<Product>,
    val status: Boolean
)