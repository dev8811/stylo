package com.Stylo.stylo.ui.home

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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

        val product = intent.getParcelableExtra("PRODUCT", Product::class.java)

        if (product == null) {
            Toast.makeText(this, "Product details not available", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set up product images with ViewPager2
        setupProductImagePager(product)

        // Set product data using ViewBinding
        binding.productTitle.text = product.productname
        binding.productPrice.text = "₹${product.originalprice}"
        binding.productRating.text = "⭐ ${product.rating}/5"
        binding.productDescription.text = product.description

        // Set up size options with ChipGroup
        setupSizeOptions(product)

        // Handle Add to Cart button click
        binding.btnAddToCart.setOnClickListener {
            if (selectedSize != null) {
                Toast.makeText(this, "Added ${product.productname} (Size: $selectedSize) to cart", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please select a size", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSizeOptions(product: Product) {
        // Clear any existing chips
        binding.chipGroupTechnologies.removeAllViews()

        // Example sizes - replace with actual sizes from your product
        val sizes = listOf("S", "M", "L", "XL")

        // Create a chip for each size
        for (size in sizes) {
            val chip = Chip(this).apply {
                text = size.trim()
                isCheckable = true
                isClickable = true

                // Set click listener for each chip
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedSize = text.toString()
                    } else if (selectedSize == text.toString()) {
                        selectedSize = null
                    }
                }
            }
            binding.chipGroupTechnologies.addView(chip)
        }
    }

    private fun setupProductImagePager(product: Product) {
        // Create a list to hold all image URLs
        val imageUrls = mutableListOf<String>()

        // Add all images from the all_images list
        if (product.all_images.isNotEmpty()) {
            imageUrls.addAll(product.all_images)
        } else {
            // Fallback to primary_image and productimage if all_images is empty
            if (product.primary_image.isNotEmpty()) {
                imageUrls.add(product.primary_image)
            }
            if (product.productimage.isNotEmpty() && product.productimage != product.primary_image) {
                imageUrls.add(product.productimage)
            }
        }

        // Ensure we have at least one image to display
        if (imageUrls.isEmpty() && product.productimage.isNotEmpty()) {
            imageUrls.add(product.productimage)
        }

        // Set up the adapter
        val adapter = ProductImageAdapter(imageUrls)
        binding.productImagePager.adapter = adapter

        // Connect the TabLayout (dots indicator) with ViewPager2
        TabLayoutMediator(binding.imageIndicator, binding.productImagePager) { _, _ ->
            // No configuration needed for simple dots
        }.attach()
    }
}