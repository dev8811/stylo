package com.Stylo.stylo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.Stylo.stylo.R
import com.Stylo.stylo.data.model.Product
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
        with(holder.binding) {
            productName.text = product.name
            productPrice.text = product.price
            productImage.setImageResource(product.imageResId)

            // Ensure different icons for favorite and non-favorite state
            favoriteIcon.setImageResource(
                if (product.isFavorite) R.drawable.heart else R.drawable.heart
            )

            favoriteIcon.setOnClickListener {
                product.isFavorite = !product.isFavorite
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount() = productList.size
}
