package com.Stylo.stylo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.Stylo.stylo.R
import com.Stylo.stylo.databinding.ItemProductCardBinding

class ProductAdapter(private val productList: List<Product>) :
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

        binding.productImage.setImageResource(product.imageResId)
        binding.productName.text = product.name
        binding.productPrice.text = product.price

        binding.favoriteIcon.setImageResource(
            if (product.isFavorite) R.drawable.heart else R.drawable.home
        )

        // Toggle favorite icon on click
        binding.favoriteIcon.setOnClickListener {
            product.isFavorite = !product.isFavorite
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = productList.size
}
