package com.Stylo.stylo.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.Stylo.stylo.R
import com.Stylo.stylo.RetrofitApi.ApiClient
import com.Stylo.stylo.RetrofitApi.CartItemResponse
import com.Stylo.stylo.RetrofitApi.FatchProduct
import com.Stylo.stylo.RetrofitApi.Product
import com.Stylo.stylo.adapter.ProductAdapter
import com.Stylo.stylo.adapter.ProductImageAdapter
import com.Stylo.stylo.databinding.ActivityProductDetailBinding
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Product_detail_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private var selectedSize: String? = null
    private var selectedColor: String? = null
    private var quantity = 1
    private var CategoryID = 0
    private var userId: Int? = null
    private var basePrice: Double = 0.0
    private val productList = mutableListOf<Product>()
    private var adapter: ProductAdapter? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Retrieve user data from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userIdString = sharedPreferences.getString("userId", null)

        // Convert userIdString to Int (handle safely)
        userId = userIdString?.toInt()

        if (userId == null) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Set up the custom action bar
        setupActionBar()

        // Get product from intent
        val product: Product? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("PRODUCT", Product::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra("PRODUCT") as? Product
        }

        if (product == null) {
            Toast.makeText(this, "Product details not available", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        CategoryID = product.category_id


        // Set base price
        basePrice = product.price.toDouble()

        // Set up product images with ViewPager2
        setupProductImagePager(product)

        // Set product data
        binding.productTitle.text = product.name
        binding.productPrice.text = "₹${basePrice * quantity}"
        binding.productDescription.text = product.description

        // Setup product recycler view for similar products
        setupProductRecyclerView()
        // Setup stock indicator
        setupStockIndicator(product)

        // Set up size options
        setupSizeOptions(product)

        // Set up quantity selector
        setupQuantitySelector()

        // Handle Add to Cart button click
        binding.btnAddToCart.setOnClickListener {
            if (selectedSize != null) {
                addToCart(userId!!, product.product_id, quantity)
            } else {
                Toast.makeText(this, "Please select a size", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setupActionBar() {
        val backButton = binding.myToolbar.btnBack
        val titleTextView = binding.myToolbar.tvTitle
        val notificationButton = binding.myToolbar.btnNotification

        titleTextView.text = "Details"

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        notificationButton.setOnClickListener {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSizeOptions(product: Product) {
        binding.chipGroupTechnologies.removeAllViews()

        val sizes = product.sizes

        if (sizes.isEmpty()) {
            Toast.makeText(this, "No sizes available", Toast.LENGTH_SHORT).show()
            return
        }

        for (size in sizes) {
            val chip = Chip(this).apply {
                text = size.toString().trim()
                isCheckable = true
                isClickable = true

                setOnCheckedChangeListener { _, isChecked ->
                    selectedSize = if (isChecked) text.toString() else null
                }
            }
            binding.chipGroupTechnologies.addView(chip)
        }
    }

    private fun setupStockIndicator(product: Product) {
        if (product.is_active) {
            binding.stockIndicator.visibility = View.GONE
        } else {
            binding.stockIndicator.text = "Out of Stock"
            binding.btnAddToCart.visibility = View.GONE
            binding.quantityText.visibility = View.GONE
            binding.btnIncrease.visibility = View.GONE
            binding.btnDecrease.visibility = View.GONE
            binding.stockIndicator.setTextColor(resources.getColor(R.color.red, null))
        }
    }

    private fun setupQuantitySelector() {
        binding.quantityText.text = quantity.toString()
        binding.productPrice.text = "₹${basePrice * quantity}"

        binding.btnIncrease.setOnClickListener {
            quantity++
            binding.quantityText.text = quantity.toString()
            binding.productPrice.text = "₹${basePrice * quantity}"
        }

        binding.btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.quantityText.text = quantity.toString()
                binding.productPrice.text = "₹${basePrice * quantity}"
            }
        }
    }

    private fun setupProductImagePager(product: Product) {
        val imageUrls = mutableListOf<String>()

        if (product.all_images.isNotEmpty()) {
            imageUrls.addAll(product.all_images)
        } else if (product.primary_image.isNotEmpty()) {
            imageUrls.add(product.primary_image)
        }

        if (imageUrls.isEmpty()) {
            imageUrls.add("android.resource://${packageName}/${R.drawable.placeholder_image}")
        }

        val adapter = ProductImageAdapter(imageUrls)
        binding.productImagePager.adapter = adapter

        TabLayoutMediator(binding.imageIndicator, binding.productImagePager) { _, _ -> }.attach()
    }

    private fun setupProductRecyclerView() {
        // Make sure the recyclerView exists in your layout
        binding.productGrid.layoutManager = GridLayoutManager(this, 2)
        binding.productGrid.setHasFixedSize(true)

        adapter = ProductAdapter(productList) { product ->
            val intent = Intent(this, Product_detail_Activity::class.java).apply {
                putExtra("PRODUCT", product)
            }
            startActivity(intent)
        }
        binding.productGrid.adapter = adapter

        // Show loading state if needed
        binding.progressBar.visibility = View.VISIBLE

        // Load related products based on category ID
        fetchProducts(CategoryID.toString())
    }

    private fun fetchProducts(categoryId: String) {
        ApiClient.apiService.getProducts(categoryId).enqueue(object : Callback<FatchProduct> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<FatchProduct>, response: Response<FatchProduct>) {
                // Hide loading state if needed
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    response.body()?.let { fetchedData ->
                        // Get the current product ID
                        val currentProductId =
                            intent.getParcelableExtra<Product>("PRODUCT")?.product_id ?: -1

                        // Filter out the current product from the similar products list
                        val filteredProducts =
                            fetchedData.products.filter { it.product_id != currentProductId }

                        // Update the product list with filtered results
                        productList.clear()
                        productList.addAll(filteredProducts)
                        adapter?.notifyDataSetChanged()

                        // Show/hide empty state
                        if (productList.isEmpty()) {
                            binding.emptyStateView.visibility = View.VISIBLE
                            binding.productGrid.visibility = View.GONE
                        } else {
                            binding.emptyStateView.visibility = View.GONE
                            binding.productGrid.visibility = View.VISIBLE
                        }
                    } ?: showToast("No products found")
                } else {
                    showToast("Failed to load products: ${response.message()}")
                    Log.e("ProductDetail", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FatchProduct>, t: Throwable) {
                // Hide loading state
                binding.progressBar.visibility = View.GONE

                showToast("Error fetching products: ${t.localizedMessage}")
                Log.e("ProductDetail", "Error fetching products", t)
            }
        })
    }

    private fun addToCart(userId: Int, productId: Int, quantity: Int) {
        ApiClient.apiService.addToCart("add_item", userId, productId, quantity)
            .enqueue(object : Callback<CartItemResponse> {
                override fun onResponse(
                    call: Call<CartItemResponse>,
                    response: Response<CartItemResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        showToast("added to cart successfully")
                        //     showToast(response.body()?.message.toString())
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        showToast("Failed to add product to cart: $errorMessage")
                        Log.e("AddToCart", "Error: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<CartItemResponse>, t: Throwable) {
                    showToast("Network error: ${t.localizedMessage}")
                    Log.e("AddToCart", "Network Error: ${t.localizedMessage}", t)
                }
            })
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}