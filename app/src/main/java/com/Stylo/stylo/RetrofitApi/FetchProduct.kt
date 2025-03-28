package com.Stylo.stylo.RetrofitApi

data class FetchProduct(
    val products: List<Product>,
    val status: Boolean
)