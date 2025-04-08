package com.Stylo.stylo.RetrofitApi

import com.google.gson.annotations.SerializedName

// Base Response Model
open class BaseResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String
)

// General Response
data class LocalResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: User? // Add user data in response
)

// User Data Model
data class User(
    @SerializedName("user_id") val userId: String,
    @SerializedName("email") val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String
)

// Authentication Requests
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class SignupRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

// Response for create/clear cart operations
data class CartResponse(
    @SerializedName("cart_id") val cartId: Int? = null
) : BaseResponse(status = true, message = "")

// Response for add/remove cart item operations
data class CartItemResponse(
    @SerializedName("cart_item_id") val cartItemId: Int? = null
) : BaseResponse(status = true, message = "")

// Response for update cart item operations
data class CartItemUpdateResponse(
    @SerializedName("cart_item_id") val cartItemId: Int? = null,
    @SerializedName("quantity") val quantity: Int? = null
) : BaseResponse(status = true, message = "")

// Cart Details Response
data class CartDetailsResponse(
    @SerializedName("cart_id") val cartId: Int? = null,
    @SerializedName("cart_items") val cartItems: List<CartItem> = emptyList(),
    @SerializedName("item_count") val itemCount: Int = 0,
    @SerializedName("total") val total: Double = 0.0
) : BaseResponse(status = true, message = "")

// Cart Item Model
data class CartItem(
    @SerializedName("cart_item_id") val cartItemId: Int,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") var quantity: Int,
    @SerializedName("subtotal") val subtotal: Double,
    @SerializedName("stock_quantity") val stockQuantity: Int,
    @SerializedName("primary_image") val primaryImage: String,
    @SerializedName("all_images") val allImages: List<String>,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("sizes") val sizes: List<String>,
    var isLoading: Boolean = false
)
data class CategoryResponse(
    val status: Boolean,
    val message: String,
    val data: List<Category>
)

// Category.kt
data class Category(
    val category_id: Int,
    val name: String
)


