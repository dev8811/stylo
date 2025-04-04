package com.Stylo.stylo.RetrofitApi

import androidx.annotation.Nullable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST("login.php")
    fun loginUser(@Body request: LoginRequest): Call<LocalResponse>

    @Headers("Content-Type: application/json")
    @POST("signup.php")
    fun signupUser(@Body request: SignupRequest): Call<LocalResponse>

    @FormUrlEncoded
    @POST("fetchProduct.php")
    fun getProducts(@Field("categoryid") categoryId: String): Call<FatchProduct>

    @FormUrlEncoded
    @POST("cart.php")
    fun addToCart(
        @Field("action") action: String = "add_item",
        @Field("user_id") userId: Int,
        @Field("product_id") productId: Int,
        @Field("quantity") quantity: Int
    ): Call<CartItemResponse>

    @FormUrlEncoded
    @POST("cart.php")
    fun updateCartItem(
        @Field("action") action: String = "update_item",
        @Field("user_id") userId: Int,
        @Field("cart_item_id") cartItemId: Int,
        @Field("quantity") quantity: Int
    ): Call<CartItemUpdateResponse>

    @FormUrlEncoded
    @POST("cart.php")
    fun removeCartItem(
        @Field("action") action: String = "remove_item",
        @Field("user_id") userId: Int,
        @Field("cart_item_id") cartItemId: Int
    ): Call<CartItemResponse>

    @FormUrlEncoded
    @POST("cart.php")
    fun getCart(
        @Field("action") action: String = "get_cart",
        @Field("user_id") userId: Int
    ): Call<CartDetailsResponse>

    @FormUrlEncoded
    @POST("cart.php")
    fun clearCart(
        @Field("action") action: String = "clear_cart",
        @Field("user_id") userId: Int
    ): Call<CartResponse>

    @POST("wishlist_toggle.php")
    fun toggleWishlist(
        @Body body: Map<String, Int>
    ): Call<BaseResponse>

}
