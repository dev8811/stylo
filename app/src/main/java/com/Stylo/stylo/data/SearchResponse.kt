package com.Stylo.stylo.data

data class SearchResponse(
    val limit: Int,
    val offset: Int,
    val products: List<Product>,
    val status: String,
    val total: Int
)