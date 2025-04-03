package com.Stylo.stylo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.Stylo.stylo.R
import com.Stylo.stylo.RetrofitApi.Product
import com.Stylo.stylo.databinding.ItemProductCardBinding
import com.bumptech.glide.Glide

class ProductAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // Track favorite states for products
    private val favoriteStates = mutableMapOf<Int, Boolean>()

    inner class ProductViewHolder(private val binding: ItemProductCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, position: Int) {
            binding.productName.text = product.name
            binding.productPrice.text = "₹${product.price}"

            // Get the primary image or fallback to the first available image
            val imageToLoad = if (product.primary_image.isNotEmpty()) {
                product.primary_image
            } else if (product.all_images.isNotEmpty()) {
                product.all_images[0]
            } else {
                R.drawable.placeholder_image // Fallback image if no images are available
            }

            Glide.with(binding.root.context)
                .load(imageToLoad)
                .placeholder(R.drawable.placeholder_image) // Placeholder while loading
                .error(R.drawable.placeholder_image) // Error image if loading fails
                .into(binding.productImage)

            // Check stock availability
            if (product.stock_quantity > 0 && product.is_active) { // ✅ Check is_active as Boolean
                binding.productStockStatus.text = "In Stock"
                binding.productStockStatus.setTextColor(binding.root.context.getColor(R.color.green))
            } else {
                binding.productStockStatus.text = "Out of Stock"
                binding.productStockStatus.setTextColor(binding.root.context.getColor(R.color.red))
            }

            // Set favorite icon based on current state
            val isFavorite = favoriteStates[position] ?: false
            binding.favoriteIcon.setImageResource(if (isFavorite) R.drawable.heart_fill else R.drawable.heart)

            // Set click listeners
            binding.root.setOnClickListener { onItemClick(product) }

            binding.favoriteIcon.setOnClickListener {
                val newFavoriteState = !(favoriteStates[position] ?: false)
                favoriteStates[position] = newFavoriteState
                binding.favoriteIcon.setImageResource(if (newFavoriteState) R.drawable.heart_fill else R.drawable.heart)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position], position)
    }

    override fun getItemCount(): Int = products.size
}
