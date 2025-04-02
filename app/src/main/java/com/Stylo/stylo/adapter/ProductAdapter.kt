package com.Stylo.stylo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.Stylo.stylo.R
import com.Stylo.stylo.RetrofitApi.Product
import com.Stylo.stylo.databinding.ItemProductCardBinding
import com.bumptech.glide.Glide

class
ProductAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // Track favorite states for products
    private val favoriteStates = mutableMapOf<Int, Boolean>()

    inner class ProductViewHolder(val binding: ItemProductCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, position: Int) {
            binding.productName.text = product.productname
            binding.productPrice.text = "₹${product.originalprice}"

            // Get the first image from the list if available, otherwise fallback to productimage
            val imageToLoad = if (product.all_images.isNotEmpty()) {
                product.all_images[0]
            } else {
                product.productimage
            }

            Glide.with(binding.root.context)
                .load(imageToLoad)
                .into(binding.productImage)

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