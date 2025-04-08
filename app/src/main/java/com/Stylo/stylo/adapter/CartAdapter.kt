package com.Stylo.stylo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Stylo.stylo.R
import com.Stylo.stylo.RetrofitApi.CartItem
import com.bumptech.glide.Glide

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onRemoveItem: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateCartItems(newItems: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductSize: TextView = itemView.findViewById(R.id.tvProductSize)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        private val btnDecrease: ImageButton = itemView.findViewById(R.id.btnDecrease)
        private val btnIncrease: ImageButton = itemView.findViewById(R.id.btnIncrease)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)

        fun bind(item: CartItem) {
            tvProductName.text = item.productName

            // Add size information if available
            tvProductSize.text = item.sizes?.let { "Size $it" } ?: ""

            // Format price with currency symbol
            tvProductPrice.text = "₹${item.price}"
            tvQuantity.text = item.quantity.toString()

            // Load product image using Glide
            Glide.with(itemView.context)
                .load(item.primaryImage)
                .placeholder(R.drawable.placeholder_image)
                .into(ivProductImage)

            btnIncrease.setOnClickListener {
                onQuantityChange(item, item.quantity + 1)
            }

            btnDecrease.setOnClickListener {
                if (item.quantity > 1) {
                    onQuantityChange(item, item.quantity - 1)
                }
            }

            btnRemove.setOnClickListener {
                onRemoveItem(item)
            }
        }
    }
}