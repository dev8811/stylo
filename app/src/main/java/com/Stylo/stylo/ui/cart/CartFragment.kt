package com.Stylo.stylo.ui.cart

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.Stylo.stylo.MainActivity
import com.Stylo.stylo.R
import com.Stylo.stylo.RetrofitApi.*
import com.Stylo.stylo.adapter.CartAdapter
import com.Stylo.stylo.databinding.FragmentCartBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>()

    private var userId: Int? = null  // Change userId type to Int?

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve user data from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userIdString = sharedPreferences.getString("userId", null)

        // Convert userIdString to Int (handle safely)
        userId = userIdString?.toIntOrNull()

        if (userId == null) {
            Toast.makeText(requireContext(), "Invalid user ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Hide Bottom Navigation
        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.GONE

        setupRecyclerView()
        fetchCartData()

        binding.btnClearCart.setOnClickListener {
            clearCart()
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            cartItems,
            onQuantityChange = { item, newQuantity ->
                updateCartQuantity(item.cartItemId, newQuantity)
            },
            onRemoveItem = { item ->
                removeCartItem(item.cartItemId)
            }
        )

        binding.recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun fetchCartData() {
        userId?.let { id ->
            ApiClient.apiService.getCart(userId = id)
                .enqueue(object : Callback<CartDetailsResponse> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call<CartDetailsResponse>, response: Response<CartDetailsResponse>) {
                        if (response.isSuccessful && response.body()?.status == true) {
                            val items = response.body()?.cartItems

                            cartItems.clear()
                            if (items != null) {
                                cartItems.addAll(items)
                            }
                            cartAdapter.notifyDataSetChanged()

                            updateCartVisibility()

                            // Calculate and update cart total
                            val total = cartItems.sumOf { item -> item.price * item.quantity }
                            binding.tvTotal.text = "₹$total"
                        } else {
                            Toast.makeText(requireContext(), "Failed to fetch cart", Toast.LENGTH_SHORT).show()
                            cartItems.clear()
                            cartAdapter.notifyDataSetChanged()
                            updateCartVisibility()
                        }
                    }

                    override fun onFailure(call: Call<CartDetailsResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        cartItems.clear()
                        cartAdapter.notifyDataSetChanged()
                        updateCartVisibility()
                    }

                    private fun updateCartVisibility() {
                        if (cartItems.isEmpty()) {
                            binding.emptyCartView.visibility = View.VISIBLE
                            binding.recyclerViewCart.visibility = View.GONE
                            binding.checkoutLayout.visibility = View.GONE
                        } else {
                            binding.emptyCartView.visibility = View.GONE
                            binding.recyclerViewCart.visibility = View.VISIBLE
                            binding.checkoutLayout.visibility = View.VISIBLE
                        }
                    }
                })
        }
    }


    private fun updateCartQuantity(cartItemId: Int, newQuantity: Int) {
        userId?.let { id ->
            ApiClient.apiService.updateCartItem(userId = id, cartItemId = cartItemId, quantity = newQuantity)
                .enqueue(object : Callback<CartItemUpdateResponse> {
                    override fun onResponse(call: Call<CartItemUpdateResponse>, response: Response<CartItemUpdateResponse>) {
                        if (response.isSuccessful) {
                            fetchCartData() // Refresh cart data and update total
                        }
                    }

                    override fun onFailure(call: Call<CartItemUpdateResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Failed to update item", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun removeCartItem(cartItemId: Int) {
        userId?.let { id ->
            ApiClient.apiService.removeCartItem(userId = id, cartItemId = cartItemId)
                .enqueue(object : Callback<CartItemResponse> {
                    override fun onResponse(call: Call<CartItemResponse>, response: Response<CartItemResponse>) {
                        if (response.isSuccessful) {
                            fetchCartData() // Refresh cart data and update total
                        }
                    }

                    override fun onFailure(call: Call<CartItemResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Failed to remove item", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun clearCart() {
        userId?.let { id ->
            ApiClient.apiService.clearCart(userId = id)
                .enqueue(object : Callback<CartResponse> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                        if (response.isSuccessful) {
                            cartItems.clear()
                            cartAdapter.notifyDataSetChanged()
                            binding.tvTotal.text = "₹0"
                            Toast.makeText(requireContext(), "Cart cleared", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Failed to clear cart", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.VISIBLE
        _binding = null
    }
}
