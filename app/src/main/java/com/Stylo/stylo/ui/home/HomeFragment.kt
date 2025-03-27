package com.Stylo.stylo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.Stylo.stylo.R
import com.Stylo.stylo.adapter.CategoryAdapter
import com.Stylo.stylo.adapter.Product
import com.Stylo.stylo.adapter.ProductAdapter
import com.Stylo.stylo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupCategoryRecyclerView()
        setupProductRecyclerView()

        return root
    }

    private fun setupCategoryRecyclerView() {
        val categoryList = listOf("All","Tshirts", "Jeans", "Shoes", "Jackets")
        val categoryAdapter = CategoryAdapter(categoryList)

        binding.categoryTabs.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryTabs.adapter = categoryAdapter
    }

    private fun setupProductRecyclerView() {
        binding.productGrid.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.productGrid.setHasFixedSize(true)

        val productList = listOf(
            Product(R.drawable.sample_product, "Regular Fit Polo", "$1,190", false),
            Product(R.drawable.sample_product, "Regular Fit Polo", "$1,190", false),
            Product(R.drawable.sample_product, "Regular Fit Polo", "$1,190", false),
            Product(R.drawable.sample_product, "Regular Fit Polo", "$1,190", false),
            Product(R.drawable.sample_product, "Regular Fit Polo", "$1,190", false),
            Product(R.drawable.sample_product, "Regular Fit Polo", "$1,190", false),
            Product(R.drawable.sample_product, "Regular Fit Polo", "$1,190", false),
            Product(R.drawable.sample_product, "Regular Fit Polo", "$1,190", false),
            Product(R.drawable.heart, "Casual T-Shirt", "$990", false),
            Product(R.drawable.home, "Slim Fit Jeans", "$2,190", false),
            Product(R.drawable.bg_vector, "Sports Shoes", "$3,290", false),
            Product(R.drawable.search, "Leather Jacket", "$4,500", false),
        )

        adapter = ProductAdapter(productList)
        binding.productGrid.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
