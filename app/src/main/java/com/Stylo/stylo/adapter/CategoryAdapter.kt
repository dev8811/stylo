package com.Stylo.stylo.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.Stylo.stylo.RetrofitApi.Category
import com.Stylo.stylo.databinding.ItemCategoryTabBinding

class CategoryAdapter(
    private val categoryList: List<Category>,
    private val onCategorySelected: (Category) -> Unit // Callback with full Category object
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    inner class CategoryViewHolder(val binding: ItemCategoryTabBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category, position: Int) {
            binding.textViewCategory.text = category.name

            // Change UI based on selection
            if (position == selectedPosition) {
                binding.textViewCategory.setTextColor(Color.WHITE)
                binding.textViewCategory.setBackgroundColor(Color.BLACK)
            } else {
                binding.textViewCategory.setTextColor(Color.BLACK)
                binding.textViewCategory.setBackgroundColor(Color.TRANSPARENT)
            }

            // Handle click
            binding.textViewCategory.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)
                onCategorySelected(category) // Pass selected category to fragment
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryTabBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position], position)
    }

    override fun getItemCount(): Int = categoryList.size
}
