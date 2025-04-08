package com.Stylo.stylo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.Stylo.stylo.R
import com.Stylo.stylo.data.Product
import com.Stylo.stylo.databinding.ItemSearchBinding
import com.bumptech.glide.Glide

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ProductViewHolder>() {

    private val products = mutableListOf<Product>()

    // For fresh search results
    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    // For loading more (pagination)
    fun appendProducts(newProducts: List<Product>) {
        val oldSize = products.size
        products.addAll(newProducts)
        notifyItemRangeInserted(oldSize, newProducts.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

    inner class ProductViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.productName.text = product.name
            binding.productPrice.text = "Price: ₹${product.price}"

            val imageUrl = product.primary_image
            Glide.with(binding.productImage.context)
                .load(imageUrl ?: "")
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(binding.productImage)


        }
    }
}
