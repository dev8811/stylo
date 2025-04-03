package com.Stylo.stylo.ui.home

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.Stylo.stylo.R
import com.Stylo.stylo.RetrofitApi.Product
import com.Stylo.stylo.adapter.ProductImageAdapter
import com.Stylo.stylo.databinding.ActivityProductDetailBinding
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator

class Product_detail_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private var selectedSize: String? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the custom action bar
        setupActionBar()

        // Get product from intent
        val product: Product? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("PRODUCT", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("PRODUCT") as? Product
        }

        if (product == null) {
            Toast.makeText(this, "Product details not available", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set up product images with ViewPager2
        setupProductImagePager(product)

        // Set product data using ViewBinding
        binding.productTitle.text = product.name
        binding.productPrice.text = "₹${product.price}"
        binding.productDescription.text = product.description

        // Set up size options with ChipGroup
        setupSizeOptions(product)

        // Handle Add to Cart button click
        binding.btnAddToCart.setOnClickListener {
            if (selectedSize != null) {
                Toast.makeText(
                    this,
                    "Added ${product.name} (Size: $selectedSize) to cart",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Please select a size", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupActionBar() {
        // Find views in the included action bar layout
        val backButton = binding.myToolbar.btnBack
        val titleTextView = binding.myToolbar.tvTitle
        val notificationButton = binding.myToolbar.btnNotification

        // Set the title
        titleTextView.text = "Details"

        // Set click listener for back button
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Set click listener for notification button
        notificationButton.setOnClickListener {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSizeOptions(product: Product) {
        binding.chipGroupTechnologies.removeAllViews()

        // ✅ FIX: Use `product.sizes` directly if available
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

    private fun setupProductImagePager(product: Product) {
        val imageUrls = mutableListOf<String>()

        // ✅ FIX: Simplified logic, avoiding duplicates
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
}
