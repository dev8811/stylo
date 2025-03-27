package com.Stylo.stylo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.Stylo.stylo.R
import com.Stylo.stylo.RetrofitApi.Product
import com.Stylo.stylo.databinding.ItemProductCardBinding

class ProductAdapter(private val productList: MutableList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

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

        // Load product image from URL
        Glide.with(binding.productImage.context)
            .load(product.productimage) // URL from API
            .placeholder(R.drawable.placeholder_image) // Fallback image
            .error(R.drawable.placeholder_image) // Error image
            .into(binding.productImage)

        binding.productName.text = product.productname
        binding.productPrice.text = "₹${product.originalprice}"

        // Toggle favorite icon
        val isFavorite = holder.itemView.tag as? Boolean ?: false
        binding.favoriteIcon.setImageResource(if (isFavorite) R.drawable.heart_fill else R.drawable.heart)

        binding.favoriteIcon.setOnClickListener {
            val newFavoriteState = !(holder.itemView.tag as? Boolean ?: false)
            holder.itemView.tag = newFavoriteState
            binding.favoriteIcon.setImageResource(if (newFavoriteState) R.drawable.heart_fill else R.drawable.heart)
        }
    }

    override fun getItemCount(): Int = productList.size

    // Update product list dynamically
    fun updateProducts(newProducts: List<Product>) {
        productList.clear()
        productList.addAll(newProducts)
        notifyDataSetChanged()
    }
}
