package com.Stylo.stylo.ui.home
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.Stylo.stylo.RetrofitApi.Product
import com.Stylo.stylo.adapter.ProductImageAdapter
import com.Stylo.stylo.databinding.ActivityProductDetailBinding
import com.google.android.material.tabs.TabLayoutMediator

class Product_detail_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

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

        // Handle Add to Cart button click
        binding.btnAddToCart.setOnClickListener {
            val checkedId = binding.sizeOptions.checkedRadioButtonId
            if (checkedId != -1) {
                val selectedSize = findViewById<RadioButton>(checkedId)?.text
                Toast.makeText(this, "Added ${product.productname} (Size: $selectedSize) to cart", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please select a size", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle Favorite button click
//        binding.btnFavorite.setOnClickListener {
//            // Toggle favorite status
//            val isFavorite = !it.isSelected
//            it.isSelected = isFavorite
//
//            val message = if (isFavorite) "Added to favorites" else "Removed from favorites"
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//        }
    }

    private fun setupProductImagePager(product: Product) {
        // For this example, we'll create a list of image URLs
        // In a real app, this might come from your product model
        val imageUrls = listOf(
            product.productimage,
            // Add more images if available, for example:
            "${product.productimage}_1",
            "${product.productimage}_2"
        )

        // Set up the adapter
        val adapter = ProductImageAdapter(imageUrls)
        binding.productImagePager.adapter = adapter

        // Connect the TabLayout (dots indicator) with ViewPager2
        TabLayoutMediator(binding.imageIndicator, binding.productImagePager) { _, _ ->
            // No configuration needed for simple dots
        }.attach()
    }
}