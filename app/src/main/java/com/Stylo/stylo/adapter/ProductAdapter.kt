package com.Stylo.stylo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.Stylo.stylo.R
import com.Stylo.stylo.RetrofitApi.Product
import com.Stylo.stylo.databinding.ItemProductCardBinding

class ProductAdapter(private val productList: MutableList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // Track favorite states for products
    private val favoriteStates = mutableMapOf<Int, Boolean>()

    inner class ProductViewHolder(val binding: ItemProductCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        val binding = holder.binding

        // Debugging: Log the image URL
        Log.d("ProductAdapter", "Loading Image URL: ${product.productimage}")

        // Load product image from URL
        Glide.with(binding.productImage.context)
            .load(product.productimage)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image)
            .into(binding.productImage)

        binding.productName.text = product.productname
        binding.productPrice.text = "₹${product.originalprice}"

        // Retrieve favorite state
        val isFavorite = favoriteStates[position] ?: false
        binding.favoriteIcon.setImageResource(if (isFavorite) R.drawable.heart_fill else R.drawable.heart)

        binding.favoriteIcon.setOnClickListener {
            val newFavoriteState = !(favoriteStates[position] ?: false)
            favoriteStates[position] = newFavoriteState
            binding.favoriteIcon.setImageResource(if (newFavoriteState) R.drawable.heart_fill else R.drawable.heart)
        }
    }

    override fun getItemCount(): Int = productList.size

    // Update product list dynamically
    fun updateProducts(newProducts: List<Product>) {
        val previousSize = productList.size
        productList.clear()
        productList.addAll(newProducts)
        notifyItemRangeChanged(0, productList.size.coerceAtLeast(previousSize))
    }
}
