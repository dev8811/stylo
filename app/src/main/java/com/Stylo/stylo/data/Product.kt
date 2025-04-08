package com.Stylo.stylo.data

data class Product(
    val category_id: String,
    val category_name: String,
    val description: String,
    val images: List<Any>,
    val name: String,
    val price: String,
    val primary_image: Any,
    val product_id: String,
    val sizes: String,
    val stock_quantity: String
)
data class Image(
    val image_id: String,
    val image_url: String,
    val is_primary: String
)