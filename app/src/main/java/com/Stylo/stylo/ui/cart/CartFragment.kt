package com.Stylo.stylo.ui.cart

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.Stylo.stylo.Bottom_Navigatio_Activity
import com.Stylo.stylo.R
import com.Stylo.stylo.RetrofitApi.ApiClient
import com.Stylo.stylo.RetrofitApi.CartDetailsResponse
import com.Stylo.stylo.RetrofitApi.CartItem
import com.Stylo.stylo.RetrofitApi.CartItemResponse
import com.Stylo.stylo.RetrofitApi.CartItemUpdateResponse
import com.Stylo.stylo.adapter.CartAdapter
import com.Stylo.stylo.databinding.FragmentCartBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>()
    private var userId: Int? = null
    private var subtotal: Double = 0.0
    private var isCheckoutInProgress = false
    private val FREE_SHIPPING_THRESHOLD = 1000.0
    private val SHIPPING_FEE = 100.0

    // Track if fragment is active to prevent callbacks after destruction
    private var isFragmentActive = false

    // Keep track of ongoing network calls to cancel them when needed
    private val activeCalls = mutableListOf<Call<*>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        isFragmentActive = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()
        loadUserData()
        setupRecyclerView()
        setupListeners()
        showLoadingState()
        fetchCartData()
    }

    private fun loadUserData() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userIdString = sharedPreferences.getString("userId", null)
        userId = userIdString?.toIntOrNull()

        if (userId == null) {
            showError("User session expired. Please login again.")
            navigateToLogin()
            return
        }
    }

    private fun navigateToLogin() {
        // Navigate to login screen - implement according to your app's navigation
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            cartItems,
            onQuantityChange = { item, newQuantity ->
                updateCartQuantity(
                    item.cartItemId,
                    newQuantity
                )
            },
            onRemoveItem = { item -> showRemoveItemConfirmation(item) })

        _binding?.recyclerViewCart?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator().apply {
                addDuration = 300
                removeDuration = 300
                changeDuration = 300
            }
            adapter = cartAdapter
        }
    }

    private fun setupListeners() {
        _binding?.btnContinueShopping?.setOnClickListener {
            // Navigate to the products/home screen
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        _binding?.btnCheckout?.setOnClickListener {
            if (!isCheckoutInProgress) {
                proceedToCheckout()
            }
        }

        _binding?.swipeRefreshLayout?.setOnRefreshListener {
            fetchCartData()
        }
    }

    private fun setupActionBar() {
        _binding?.myToolbar?.let { toolbar ->
            toolbar.tvTitle.text = "My Cart"

            toolbar.btnBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            toolbar.btnNotification.setOnClickListener {
                // Navigate to notifications
                _binding?.let { binding ->
                    Snackbar.make(binding.root, "Notifications coming soon", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun showLoadingState() {
        _binding?.let { binding ->
            binding.loadingView.visibility = View.VISIBLE
            binding.emptyCartView.visibility = View.GONE
            binding.cartContentContainer.visibility = View.GONE
        }
    }

    private fun fetchCartData() {
        if (!isFragmentActive) return

        userId?.let { id ->
            val call = ApiClient.apiService.getCart(userId = id)
            activeCalls.add(call)

            call.enqueue(object : Callback<CartDetailsResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<CartDetailsResponse>, response: Response<CartDetailsResponse>
                ) {
                    // Remove from active calls list
                    activeCalls.remove(call)

                    // Check if fragment is still active and binding exists
                    if (!isFragmentActive || _binding == null) return

                    _binding?.let { binding ->
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.loadingView.visibility = View.GONE

                        if (response.isSuccessful && response.body()?.status == true) {
                            val items = response.body()?.cartItems

                            cartItems.clear()
                            if (items != null) {
                                cartItems.addAll(items)
                            }
                            cartAdapter.notifyDataSetChanged()

                            updateCartViews()
                            calculateAndDisplayTotals()
                        } else {
                            handleApiError("Couldn't fetch your cart right now")
                        }
                    }
                }

                override fun onFailure(call: Call<CartDetailsResponse>, t: Throwable) {
                    // Remove from active calls list
                    activeCalls.remove(call)

                    // Check if fragment is still active and binding exists
                    if (!isFragmentActive || _binding == null) return

                    _binding?.let { binding ->
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.loadingView.visibility = View.GONE
                        handleApiError("Network error: ${t.message}")
                    }
                }
            })
        } ?: run {
            _binding?.let { binding ->
                binding.swipeRefreshLayout.isRefreshing = false
                binding.loadingView.visibility = View.GONE
                handleApiError("User ID not found")
            }
        }
    }

    private fun updateCartViews() {
        _binding?.let { binding ->
            if (cartItems.isEmpty()) {
                binding.emptyCartView.visibility = View.VISIBLE
                binding.cartContentContainer.visibility = View.GONE
                binding.btnCheckout.isEnabled = false
            } else {
                binding.emptyCartView.visibility = View.GONE
                binding.cartContentContainer.visibility = View.VISIBLE
                binding.recyclerViewCart.visibility = View.VISIBLE
                binding.summaryCard.visibility = View.VISIBLE
                binding.btnCheckout.visibility = View.VISIBLE
                binding.btnCheckout.isEnabled = true
            }
        }
    }

    private fun calculateAndDisplayTotals() {
        if (!isFragmentActive || _binding == null) return

        _binding?.let { binding ->
            subtotal = cartItems.sumOf { item -> item.price * item.quantity }
            binding.tvSubtotal.text = "₹${String.format("%.2f", subtotal)}"

            // Determine shipping fee based on subtotal
            val shippingFee = if (subtotal >= FREE_SHIPPING_THRESHOLD) 0.0 else SHIPPING_FEE
            binding.tvShipping.text =
                if (shippingFee == 0.0) "FREE" else "₹${String.format("%.2f", shippingFee)}"

            // Show free shipping message if applicable
            binding.freeShippingMessage.visibility =
                if (subtotal >= FREE_SHIPPING_THRESHOLD) View.VISIBLE else View.GONE

            // If not eligible for free shipping, show how much more needed
            if (subtotal < FREE_SHIPPING_THRESHOLD) {
                val remaining = FREE_SHIPPING_THRESHOLD - subtotal
                binding.freeShippingEligibility.visibility = View.VISIBLE
                binding.freeShippingEligibility.text =
                    "Add ₹${String.format("%.2f", remaining)} more to get FREE shipping!"

                // Update progress bar
                val progress = ((subtotal / FREE_SHIPPING_THRESHOLD) * 100).toInt().coerceIn(0, 100)
                animateProgressBar(progress)
            } else {
                binding.freeShippingEligibility.visibility = View.GONE
                binding.shippingProgressBar.progress = 100
            }

            // Calculate total
            val total = subtotal + shippingFee

            // Animate the total change
            animateTextChange(binding.tvTotal, "₹${String.format("%.2f", total)}")

            // Update the checkout button text to include total
            if (!isCheckoutInProgress) {
                binding.btnCheckout.text = "Checkout • ₹${String.format("%.2f", total)}"
            }
        }
    }

    private fun animateProgressBar(targetProgress: Int) {
        _binding?.let { binding ->
            val animator = ValueAnimator.ofInt(binding.shippingProgressBar.progress, targetProgress)
            animator.duration = 500
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.addUpdateListener { animation ->
                // Check if binding still exists before updating UI
                if (_binding != null) {
                    binding.shippingProgressBar.progress = animation.animatedValue as Int
                } else {
                    animator.cancel()
                }
            }
            animator.start()
        }
    }

    private fun animateTextChange(view: View, newText: String) {
        if (!isFragmentActive) return

        view.alpha = 0.3f
        val animation = view.animate().alpha(1.0f).setDuration(300)
        if (view is android.widget.TextView) {
            view.text = newText
        }
        animation.start()
    }

    private fun updateCartQuantity(cartItemId: Int, newQuantity: Int) {
        if (!isFragmentActive) return

        if (newQuantity <= 0) {
            showRemoveItemConfirmation(cartItems.first { it.cartItemId == cartItemId })
            return
        }

        // Show loading indicator on the specific item
        val position = cartItems.indexOfFirst { it.cartItemId == cartItemId }
        if (position != -1) {
            cartItems[position].isLoading = true
            cartAdapter.notifyItemChanged(position)
        }

        userId?.let { id ->
            val call = ApiClient.apiService.updateCartItem(
                userId = id, cartItemId = cartItemId, quantity = newQuantity
            )
            activeCalls.add(call)

            call.enqueue(object : Callback<CartItemUpdateResponse> {
                override fun onResponse(
                    call: Call<CartItemUpdateResponse>, response: Response<CartItemUpdateResponse>
                ) {
                    // Remove from active calls list
                    activeCalls.remove(call)

                    // Check if fragment is still active and binding exists
                    if (!isFragmentActive || _binding == null) return

                    if (position != -1 && position < cartItems.size) {
                        cartItems[position].isLoading = false
                        cartAdapter.notifyItemChanged(position)
                    }

                    if (response.isSuccessful && response.body()?.status == true) {
                        // Update local cart item
                        if (position != -1 && position < cartItems.size) {
                            cartItems[position].quantity = newQuantity
                            calculateAndDisplayTotals()
                        }

                        _binding?.let { binding ->
                            Snackbar.make(
                                binding.root, "Cart updated successfully", Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        handleApiError("Couldn't update cart")
                        fetchCartData() // Refresh to get current state
                    }
                }

                override fun onFailure(call: Call<CartItemUpdateResponse>, t: Throwable) {
                    // Remove from active calls list
                    activeCalls.remove(call)

                    // Check if fragment is still active and binding exists
                    if (!isFragmentActive || _binding == null) return

                    if (position != -1 && position < cartItems.size) {
                        cartItems[position].isLoading = false
                        cartAdapter.notifyItemChanged(position)
                    }
                    handleApiError("Network error while updating cart")
                    fetchCartData() // Refresh to get current state
                }
            })
        }
    }

    private fun showRemoveItemConfirmation(item: CartItem) {
        if (!isFragmentActive) return

        MaterialAlertDialogBuilder(requireContext()).setTitle("Remove Item")
            .setMessage("Are you sure you want to remove ${item.productName} from your cart?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.setPositiveButton("Remove") { _, _ ->
                removeCartItem(item.cartItemId)
            }.show()
    }

    private fun removeCartItem(cartItemId: Int) {
        if (!isFragmentActive || _binding == null) return

        val position = cartItems.indexOfFirst { it.cartItemId == cartItemId }
        if (position != -1) {
            // Store item details for potential undo
            val removedItem = cartItems[position]

            // Optimistically remove from UI for better UX
            cartItems.removeAt(position)
            cartAdapter.notifyItemRemoved(position)
            updateCartViews()
            calculateAndDisplayTotals()

            userId?.let { id ->
                val call = ApiClient.apiService.removeCartItem(userId = id, cartItemId = cartItemId)
                activeCalls.add(call)

                call.enqueue(object : Callback<CartItemResponse> {
                    override fun onResponse(
                        call: Call<CartItemResponse>, response: Response<CartItemResponse>
                    ) {
                        // Remove from active calls list
                        activeCalls.remove(call)

                        // Check if fragment is still active and binding exists
                        if (!isFragmentActive || _binding == null) return

                        _binding?.let { binding ->
                            if (response.isSuccessful && response.body()?.status == true) {
                                // Show snackbar with undo option
                                Snackbar.make(
                                    binding.root,
                                    "${removedItem.productName} removed from cart",
                                    Snackbar.LENGTH_LONG
                                ).setAction("UNDO") {
                                    // Add item back to cart API call would go here
                                    fetchCartData() // For simplicity, just refresh the cart
                                }.show()
                            } else {
                                // Revert optimistic update on failure
                                handleApiError("Couldn't remove item")
                                fetchCartData()
                            }
                        }
                    }

                    override fun onFailure(call: Call<CartItemResponse>, t: Throwable) {
                        // Remove from active calls list
                        activeCalls.remove(call)

                        // Check if fragment is still active and binding exists
                        if (!isFragmentActive || _binding == null) return

                        // Revert optimistic update on failure
                        handleApiError("Network error")
                        fetchCartData()
                    }
                })
            }
        }
    }

    private fun proceedToCheckout() {
        if (!isFragmentActive || _binding == null) return

        _binding?.let { binding ->
            if (cartItems.isEmpty()) {
                Snackbar.make(binding.root, "Your cart is empty", Snackbar.LENGTH_SHORT).show()
                return
            }

            isCheckoutInProgress = true
            binding.btnCheckout.isEnabled = false
            binding.btnCheckout.text = "Processing..."

            val total = subtotal + (if (subtotal >= FREE_SHIPPING_THRESHOLD) 0.0 else SHIPPING_FEE)

            // Simulate checkout process (replace with actual implementation)
            binding.root.postDelayed({
                // Check if fragment is still active and binding exists
                if (!isFragmentActive || _binding == null) return@postDelayed

                isCheckoutInProgress = false
                binding.btnCheckout.isEnabled = true
                binding.btnCheckout.text = "Checkout • ₹${String.format("%.2f", total)}"

                // Navigate to checkout screen
                // Add your navigation code here
                Snackbar.make(binding.root, "Navigating to checkout...", Snackbar.LENGTH_SHORT).show()
            }, 1500)
        }
    }

    private fun handleApiError(message: String) {
        _binding?.let { binding ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                .setAction("Retry") { fetchCartData() }.show()
        }
    }

    private fun showError(message: String) {
        if (isAdded && context != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        // Cancel all active network calls
        for (call in activeCalls) {
            call.cancel()
        }
        activeCalls.clear()

        // Set fragment inactive flag to prevent callback processing
        isFragmentActive = false

        // Show bottom navigation back if we had hidden it
        try {
            (requireActivity() as? Bottom_Navigatio_Activity)?.let { activity ->
                val bottomNav = activity.findViewById<BottomNavigationView>(R.id.nav_view)
                bottomNav?.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            // Handle any potential exceptions during cleanup
        }

        // Clear binding
        _binding = null

        super.onDestroyView()
    }
}